package ua.com.foxminded.university.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.service.GroupService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RestController("GroupRest")
@RequestMapping("/api/groups")
public class GroupController {

    private GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public Slice<Group> getAll(@PageableDefault(sort = "id") Pageable pageable) {
        log.debug("Get groups page: {} with size: {} sorting: {}", pageable.getPageNumber(),
                pageable.getPageSize(), pageable.getSort());
        return new Slice<>(groupService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public Group get(@PathVariable int id) {
        log.debug("Get group by id: {}", id);
        return groupService.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Group group) {
        log.debug("Create group: {}", group);
        groupService.create(group);
        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(group.getId())
                .toUriString();

        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, location).build();
    }

    @PutMapping("/{id}")
    public Group update(@Valid @RequestBody Group group, @PathVariable Integer id) {
        group.setId(id);
        log.debug("Update group: {}", group);
        return groupService.update(group);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        groupService.get(id);
        groupService.delete(id);
        log.debug("Delete group by id: {}", id);
    }
}
