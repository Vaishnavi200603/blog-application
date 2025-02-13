package com.mountblue.blog_application.service;

import com.mountblue.blog_application.model.Tag;
import com.mountblue.blog_application.repository.TagRepository;
import org.springframework.stereotype.Service;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

//    public Tag getOrCreateNewTag(String tagName){
//        Tag tag = tagRepository.findByName(tagName);
//        if(tag == null){
//            tag = new Tag();
//            tag.setName(tagName);
//            return tagRepository.save(tag);
//        }
//        return tag;
//    }
}
