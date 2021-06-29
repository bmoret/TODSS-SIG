package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.security.application.UserService;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.security.domain.UserRole;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.builder.SessionDirector;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.session.SessionRequest;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessionService {
    private final SessionRepository SESSION_REPOSITORY;
    private final SpecialInterestGroupService SIG_SERVICE;
    private final UserService USER_SERVICE;
    private final PersonService personService;

    public SessionService(
            SessionRepository sessionRepository,
            SpecialInterestGroupService sigService,
            UserService userService,
            PersonService personService) {
        this.SESSION_REPOSITORY = sessionRepository;
        this.SIG_SERVICE = sigService;
        this.USER_SERVICE = userService;
        this.personService = personService;
    }

    public List<Session> getAllSessions(User user) {
        Person person = user.getPerson();
        List<Session> correctSessions = new ArrayList<>();
        boolean getsAdded = false;

        for (Session session : SESSION_REPOSITORY.findAll()) {
            if (user.getRole() == UserRole.ROLE_ORGANIZER || user.getRole() == UserRole.ROLE_MANAGER) {
                if ((session.getState() == SessionState.TO_BE_PLANNED) || (session.getState() == SessionState.DRAFT)) {
                    if (person.getOrganisedSpecialInterestGroups().contains(session.getSig()) ||
                            person.getManagedSpecialInterestGroups().contains(session.getSig())) {
                        getsAdded = true;
                    }
                } else if (
                        (session.getState() != SessionState.TO_BE_PLANNED ||
                        session.getState() != SessionState.DRAFT)
                ) {
                    getsAdded = true;
                }
            } else if (user.getRole() == UserRole.ROLE_SECRETARY) {
                if (session.getState() != SessionState.DRAFT) {
                    getsAdded = true;
                }
            } else if (user.getRole() == UserRole.ROLE_ADMINISTRATOR) {
                getsAdded = true;
            } else {
                if (
                        !(session.getState().equals(SessionState.TO_BE_PLANNED)) &&
                        !(session.getState().equals(SessionState.DRAFT))
                ) {
                    getsAdded = true;
                }
            }

            if (getsAdded) {
                correctSessions.add(session);
            }

        }

        return correctSessions;
    }

    public Session getSessionById(UUID sessionId) throws NotFoundException {
        return this.SESSION_REPOSITORY.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("No session found with given id"));
    }

    public Session createSession(SessionRequest sessionRequest) throws NotFoundException {
        SpecialInterestGroup sig = this.SIG_SERVICE.getSpecialInterestGroupById(sessionRequest.sigId);
        Person person = null;
        if (sessionRequest.contactPerson != null) {
            person = personService.getPersonById(sessionRequest.contactPerson);
        }
        Session session = SessionDirector.build(sessionRequest, sig, person);
        return this.SESSION_REPOSITORY.save(session);
    }

    public Session updateSession(UUID sessionId, SessionRequest sessionRequest) throws NotFoundException {
        Session session = getSessionById(sessionId);
        Person person = null;
        if (sessionRequest.contactPerson != null) {
            person = personService.getPersonById(sessionRequest.contactPerson);
        }
        SpecialInterestGroup sig = this.SIG_SERVICE.getSpecialInterestGroupById(sessionRequest.sigId);
        Session updatedSession = SessionDirector.update(session, sessionRequest, sig, person);
        if (!updatedSession.getClass().isAssignableFrom(session.getClass())) {
            this.deleteSession(session.getId());
        }
        return this.SESSION_REPOSITORY.save(updatedSession);
    }

    public void deleteSession(UUID sessionId) throws NotFoundException {
        if (!this.SESSION_REPOSITORY.existsById(sessionId)) {
            throw new NotFoundException("No session found with given id");
        }
        this.SESSION_REPOSITORY.deleteById(sessionId);
    }

    public Session planSession(
            UUID sessionId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) throws NotFoundException {
        Session session = getSessionById(sessionId);
        if (session.getState() != SessionState.TO_BE_PLANNED) {
            throw new IllegalStateException("Session can only be planned if session state is TO_BE_PLANNED");
        }
        checkDates(startDate, endDate);
        session.getDetails().setStartDate(startDate);
        session.getDetails().setEndDate(endDate);
        session.nextState();
        return SESSION_REPOSITORY.save(session);
    }

    private void checkDates(LocalDateTime startDate, LocalDateTime endDate) {
        checkDatesNotNull(startDate, endDate);
        checkDatesBeforeNow(startDate, endDate);
        checkForSessionStartBeforeEnd(startDate, endDate);
        checkForSessionDuration(startDate, endDate);
    }

    private void checkDatesNotNull(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be empty");
        }
    }

    private void checkForSessionStartBeforeEnd(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must come before the end date");
        }
    }

    private void checkForSessionDuration(LocalDateTime startDate, LocalDateTime endDate) {
        final int MAXIMUM_SESSION_LENGTH_IN_MS = 604800000;
        if (Math.abs(Duration.between(endDate, startDate).toMillis()) > MAXIMUM_SESSION_LENGTH_IN_MS) {
            throw new IllegalArgumentException(
                    String.format(
                            "Session duration cannot be longer than %s milliseconds",
                            MAXIMUM_SESSION_LENGTH_IN_MS)
            );
        }
    }

    private void checkDatesBeforeNow(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isBefore(LocalDateTime.now()) || endDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Dates must be after now");
        }
    }

    public Session requestSessionToBePlanned(UUID sessionId) throws NotFoundException {
        Session session = getSessionById(sessionId);
        if (session.getState() != SessionState.DRAFT) {
            throw new IllegalStateException("Session can only be requested for planning as a draft");
        }
        session.nextState();
        return session;
    }

    public void addAttendeeToSession(Session session, Attendance attendance) {
        session.addAttendee(attendance);
        SESSION_REPOSITORY.save(session);
    }

    public void removeAttendeeFromSession(Session session, Person person) {
        session.removeAttendee(person);
        SESSION_REPOSITORY.save(session);
    }

    private boolean isAuthorizedForSession(String username, Session session) {
        User user;
        try {
            user = this.USER_SERVICE.getUserByUsername(username);
        } catch (NotFoundException e) {
            return false;
        }
        List<SpecialInterestGroup> relatedSigs = new ArrayList<>(user.getPerson().getOrganisedSpecialInterestGroups());
        relatedSigs.addAll(user.getPerson().getManagedSpecialInterestGroups());
        return relatedSigs.stream().anyMatch(session.getSig()::equals)
                || session.getState().equals(SessionState.TO_BE_PLANNED)
                && (user.getRole().equals(UserRole.ROLE_SECRETARY) ||
                user.getRole().equals(UserRole.ROLE_ADMINISTRATOR));
    }

    public List<Session> getAllFutureSessions(String username) {
        List<Session> sessions = this.SESSION_REPOSITORY.findAll().stream()
                .filter(session -> (session.getDetails().getStartDate().isAfter(LocalDateTime.now())
                        && (session.getState().equals(SessionState.PLANNED) ||
                        session.getState().equals(SessionState.ONGOING)))
                        || isAuthorizedForSession(username, session)
                        && (session.getState().equals(SessionState.TO_BE_PLANNED) ||
                        session.getState().equals(SessionState.DRAFT)))
                .sorted(Comparator.comparing(session -> session.getDetails().getStartDate()))
                .collect(Collectors.toList());
        return sessions.subList(0, Math.min(sessions.size(), 15));
    }

    public List<Session> getAllHistoricalSessions(String username) {
        return this.SESSION_REPOSITORY.findAll().stream()
                .filter(session -> session.getDetails().getStartDate().isBefore(LocalDateTime.now())
                        && session.getDetails().getStartDate().isAfter(
                                LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).withMonth(1))
                )
                .filter(session -> !(session.getState().equals(SessionState.DRAFT)
                        || session.getState().equals(SessionState.TO_BE_PLANNED))
                        || isAuthorizedForSession(username, session))
                .collect(Collectors.toList());
    }

    private boolean isNotAuthorizedToUserResources(String username, Person person) throws NotFoundException {
        User user = this.USER_SERVICE.getUserByUsername(username);
        Person userPerson = user.getPerson();
        Person personManager = person.getSupervisor();

        return !(
                userPerson.equals(person)
                        || (personManager != null && personManager.equals(userPerson))
        );
    }

    public List<Session> getFutureSessionsOfPerson(
            String username,
            UUID personId) throws NotFoundException, IllegalAccessException {
        Person person = personService.getPersonById(personId);
        if (isNotAuthorizedToUserResources(username, person)) {
            throw new IllegalAccessException("User is not allowed to access resources");
        }
        Predicate<Session> predicate = session -> session.getDetails().getStartDate().isAfter(LocalDateTime.now());

        return filterPersonsSessions(person, predicate);
    }

    public List<Session> getHistorySessionsOfPerson(
            String username,
            UUID personId) throws NotFoundException, IllegalAccessException {
        Person person = personService.getPersonById(personId);
        if (isNotAuthorizedToUserResources(username, person)) {
            throw new IllegalAccessException("User is not allowed to access resources");
        }
        Predicate<Session> predicate = session -> session.getDetails().getStartDate().isBefore(LocalDateTime.now())
                && session.getDetails().getStartDate().isAfter(
                        LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).withMonth(1)
        );

        return filterPersonsSessions(person, predicate);
    }

    private List<Session> filterPersonsSessions(Person person, Predicate<Session> predicate) {
        List<Session> sessions = person.getAttendance().stream()
                .map(Attendance::getSession)
                .collect(Collectors.toList());
        sessions.addAll(getPersonSigsSessions(person.getManagedSpecialInterestGroups()));
        sessions.addAll(getPersonSigsSessions(person.getOrganisedSpecialInterestGroups()));
        return sessions.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    private List<Session> getPersonSigsSessions(List<SpecialInterestGroup> sigs) {
        return sigs.stream()
                .map(SpecialInterestGroup::getSessions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }
}
