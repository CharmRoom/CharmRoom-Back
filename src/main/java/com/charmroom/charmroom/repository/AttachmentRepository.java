package com.charmroom.charmroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.charmroom.charmroom.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {

}
