package com.jli.workflow.metadata.task;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

@Getter
@Setter
public class JoinTask extends SimpleTask {
    private Pair<String, String> joining;
}
