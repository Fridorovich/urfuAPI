package org.urfu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.urfu.enums.EducationLevel;
import org.urfu.enums.EducationStandard;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "programs")
public class Program {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "cypher", nullable = false, length = 50)
    private String cypher;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private EducationLevel level;

    @Enumerated(EnumType.STRING)
    @Column(name = "standard", nullable = false)
    private EducationStandard standard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institute_uuid", nullable = false)
    private Institute institute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_uuid", nullable = false)
    private Head head;

    @Column(name = "accreditation_date", nullable = false)
    private LocalDate accreditationDate;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "program_modules",
            joinColumns = @JoinColumn(name = "program_uuid"),
            inverseJoinColumns = @JoinColumn(name = "module_uuid")
    )
    private List<Module> modules;
}