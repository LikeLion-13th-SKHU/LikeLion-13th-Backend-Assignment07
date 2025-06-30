package com.likelion.likelionassignment07.member.domain.repository;

import com.likelion.likelionassignment07.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
