package co.id.project.dhimas.onlineshop.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Response extends Serializable {
}
