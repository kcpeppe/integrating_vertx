package com.jclarity.safepoint.parser;

public interface SafepointRules extends Tokens {

    //[0.691s][info][safepoint    ] Application time: 0.0008500 seconds
    SafepointParseRule APPLICATION_TIME = new SafepointParseRule("Application time","Application time: " + TIME_STAMP);

    //[0.691s][info][safepoint    ] Entering safepoint region: RevokeBias
    SafepointParseRule ENTERING_SAFEPOINT_REGION = new SafepointParseRule("Entering Safepoint region", "Entering safepoint region: (\\S+)");

    //[0.692s][info][safepoint    ] Leaving safepoint region
    SafepointParseRule LEAVING_SAFEPOINT_REGION = new SafepointParseRule("Leaving safepoint region", "Leaving safepoint region");

    //[0.692s][info][safepoint    ] Total time for which application threads were stopped: 0.0000636 seconds, Stopping threads took: 0.0000187 seconds
    SafepointParseRule SAFEPOINT_DETAILS = new SafepointParseRule("Total safepoint time",
            "Total time for which application threads were stopped: " + TIME_STAMP + ", Stopping threads took: " + TIME_STAMP);
}
