package org.speech4j.contentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "contents")
public class ContentBox extends RepresentationModel<ContentBox> {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String guid;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "content_tag",
            joinColumns = { @JoinColumn(name = "content_guid") },
            inverseJoinColumns = { @JoinColumn(name = "tag_guid") }
    )
    private List<Tag> tags;
    private String contentUrl;
    private String transcript;
    private String tenantGuid;
}
