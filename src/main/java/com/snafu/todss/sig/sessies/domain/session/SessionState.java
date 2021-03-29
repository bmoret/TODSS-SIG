package com.snafu.todss.sig.sessies.domain.session;

public enum SessionState {
    DRAFT {
        @Override
        public SessionState next() {
            return TO_BE_PLANNED;
        }
    },
    TO_BE_PLANNED {
        @Override
        public SessionState next() {
            return PLANNED;
        }
    },
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
