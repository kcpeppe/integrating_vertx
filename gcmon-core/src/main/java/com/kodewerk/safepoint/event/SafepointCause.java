package com.kodewerk.safepoint.event;

public enum SafepointCause {
    BulkRevokeBias, CGC_Operation, Cleanup,
    Deoptimize, EnableBiasedLocking, Exit,
    G1CollectForAllocation, RevokeBias, UNKNOWN
}
