package com.mountblue.blog_application.repository;

import com.mountblue.blog_application.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tags, Long> {
    Tags findByName(String name);
}
