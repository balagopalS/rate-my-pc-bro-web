package com.ratemypcbro.context;

import com.ratemypcbro.dto.ToolCallTrace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToolCallContext {

    private static final ThreadLocal<List<ToolCallTrace>> TRACE_HOLDER =
        ThreadLocal.withInitial(ArrayList::new);

    public static void addTrace(ToolCallTrace trace) {
        TRACE_HOLDER.get().add(trace);
    }

    public static List<ToolCallTrace> getTraces() {
        return new ArrayList<>(TRACE_HOLDER.get());
    }

    public static int getNextStepNumber() {
        return TRACE_HOLDER.get().size() + 1;
    }

    public static void clear() {
        TRACE_HOLDER.get().clear();
        TRACE_HOLDER.remove();
    }
}
