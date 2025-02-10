package com.mountblue.blog_application.service;

import com.mountblue.blog_application.model.Posts;
import com.mountblue.blog_application.model.Tags;
import com.mountblue.blog_application.repository.PostRepository;
import com.mountblue.blog_application.repository.TagRepository;
import org.springframework.stereotype.Service;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tags getOrCreateNewTag(String tagName){
        Tags tag = tagRepository.findByName(tagName);
        if(tag == null){
            tag = new Tags();
            tag.setName(tagName);
            return tagRepository.save(tag);
        }
        return tag;
    }
}
