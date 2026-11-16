package com.webshop.category;

import com.webshop.common.util.TextNormalize;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "categories",
uniqueConstraints = @UniqueConstraint(
        name = "uk_categories_name",
        columnNames = "name"
    )
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    public Category(String name){
        rename(name);
    }

    public void rename(String name){
        name = TextNormalize.normalize(name);
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Category name cannot be blank");
        }
        if(name.length() > 100){
            throw new IllegalArgumentException("Category name cannot be exceed than 100 characters");
        }
        this.name = name;
    }
}
