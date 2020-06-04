package org.speech4j.contentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "content_contents")
public class Content implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String guid;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "content_content_tag",
            joinColumns = { @JoinColumn(name = "content_guid") },
            inverseJoinColumns = { @JoinColumn(name = "tag_guid") }
    )
    private List<Tag> tags;
    @Column(name = "contenturl")
    private String contentUrl;
    private String transcript;
    @Column(name = "tenantguid")
    private String tenantGuid;
}
