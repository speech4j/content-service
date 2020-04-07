package org.speech4j.contentservice.util;

import org.speech4j.contentservice.dto.request.ContentRequestDto;
import org.speech4j.contentservice.dto.request.TagDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataUtil {

    public static List<ContentRequestDto> getListOfContents() {
        List<ContentRequestDto> list = new ArrayList<>();

        List<TagDto> tags1 = Arrays.asList(new TagDto("#nightcore"), new TagDto("#music"));

        ContentRequestDto content1 = new ContentRequestDto();
        content1.setContentUrl("https://www.youtube.com/watch?v=zYoG7vTLpZk");
        content1.setTags(tags1);
        content1.setTranscript(
                "Can't see the stars, but we're reaching\n" +
                "Trying to get through the dark on a feeling\n" +
                "Lost our gravity, now we’re weightless\n" +
                "But I know in my heart we can take this");
        list.add(content1);


        List<TagDto> tags2 = Arrays.asList(new TagDto("#nightcore"), new TagDto("#relax"));
        ContentRequestDto content2 = new ContentRequestDto();
        content2.setContentUrl("https://www.youtube.com/watch?v=i4Nn6Gx2Uv8");
        content2.setTags(tags2);
        content2.setTranscript(
                "There was a time, I used to look into my father's eyes\n" +
                "In a happy home, I was a king I had a golden throne\n" +
                "Those days are gone, now the memories are on the wall\n" +
                "I hear the sounds from the places where I was born");
        list.add(content2);

        return list;
    }

}
