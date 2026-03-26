package com.ratemypcbro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareVerdict {
    private String software;
    private String score;
    private String verdict;
    private String performance_notes;
}
