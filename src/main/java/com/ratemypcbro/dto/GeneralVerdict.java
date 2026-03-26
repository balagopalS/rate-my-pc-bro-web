package com.ratemypcbro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralVerdict {
    private String rating;
    private String verdict;
    private String roast;
}
