package org.speech4j.contentservice.util;

import org.speech4j.contentservice.dto.TagDto;
import org.speech4j.contentservice.dto.request.ContentRequestDto;

import java.util.Arrays;
import java.util.List;

public class DataFixture {
    public static List<ContentRequestDto> getListOfContents() {
        List<TagDto> tags1 = Arrays.asList(new TagDto("#nightcore"), new TagDto("#music"));
        ContentRequestDto content1 = new ContentRequestDto();
        content1.setContentUrl("https://www.youtube.com/watch?v=zYoG7vTLpZk");
        content1.setTags(tags1);
        content1.setTranscript("Can not see the stars, but we are reaching, trying to get through the dark on a feeling.");

        List<TagDto> tags2 = Arrays.asList(new TagDto("#relax"), new TagDto("#mix"));
        ContentRequestDto content2 = new ContentRequestDto();
        content2.setContentUrl("https://www.youtube.com/watch?v=i4Nn6Gx2Uv8");
        content2.setTags(tags2);
        content2.setTranscript(
                "There was a time, I used to look into my eyes in a happy home, I was a king I had a golden throne.");

        return Arrays.asList(content1, content2);
    }
}
