package com.snafu.todss.sig.sessies.util;

import java.util.Arrays;
import java.util.Objects;

public class InputValidations {
    private InputValidations() {
    }

    public static void inputNotNull(Object... inputs) {
        Arrays.stream(inputs)
                .forEach(input -> {
                    if (Objects.isNull(input)) throw new IllegalArgumentException();
                } );
    }
}
