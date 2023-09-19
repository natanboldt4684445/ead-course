package com.ead.course.clients;

import com.ead.course.dtos.CourseUserDto;
import com.ead.course.dtos.ResponsePageDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Log4j2
@Component
public class AuthUserClient {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UtilsService utilsService;

    @Value("${ead.api.url.authuser}")
    String REQUEST_URL_AUTHUSER;

    public Page<UserDto> getAllUsersByCourse(UUID courseId, Pageable pageable) {
        ResponseEntity<ResponsePageDto<UserDto>> result = null;
        String url = REQUEST_URL_AUTHUSER+utilsService.createUrlGetAllUsersByCourse(courseId, pageable);
        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);

        try {
            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType = new ParameterizedTypeReference<ResponsePageDto<UserDto>>() {};

            result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            log.debug("Response Number of Elements: {}", result.getBody().getNumberOfElements());
        } catch(HttpStatusCodeException e) {
            log.error("Error request /users {} ", e);
        }
        log.info("Ending request /users courseId {} ", courseId);
        return result.getBody();
    }

    public ResponseEntity<UserDto> getOneUserById(UUID userId) {
        String url = REQUEST_URL_AUTHUSER + "/users/" + userId;
        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);

        return restTemplate.exchange(url, HttpMethod.GET, null, UserDto.class);
    }

    public CourseUserDto postSubscriptionUserInCourse(CourseModel courseModel, UUID userId) {
        String url = REQUEST_URL_AUTHUSER + "/users/" + userId + "/courses/subscription";
        CourseUserDto courseUserDto = new CourseUserDto();
        courseUserDto.setCourseId(courseModel.getCourseId());
        courseUserDto.setUserId(userId);

        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);

        return restTemplate.postForObject(url, courseUserDto, CourseUserDto.class);
    }

    public void deleteCourseUserByCourseId(UUID courseId) {
        String url = REQUEST_URL_AUTHUSER + "/users/courses/" + courseId;
        restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
    }
}
