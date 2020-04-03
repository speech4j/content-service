package com.speech4j.contentservice.util;

import com.speech4j.contentservice.dto.request.ContentRequestDto;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    public static List<ContentRequestDto> getListOfTenants() {
        List<ContentRequestDto> list = new ArrayList<>();

        ContentRequestDto content1 = new ContentRequestDto();
        list.add(content1);
        ContentRequestDto content2 = new ContentRequestDto();
        list.add(content2);

        return list;
    }

}
