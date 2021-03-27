package com.snafu.todss.sig.sessies.domain.session;

public enum SessionState {
    PLANNED {
        @Override
        public SessionState next() {
            return ONGOING;
        }
    },
    ONGOING {
        @Override
        public SessionState next() {
            return ENDED;
        }
    },
    ENDED {
        @Override
        public SessionState next() {
            return ENDED;
        }
    };

    public abstract SessionState next();
}
