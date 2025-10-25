package co.id.project.dhimas.onlineshop.base.data;

import co.id.project.dhimas.onlineshop.base.Request;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("data")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public interface BaseDataRequest extends Request {
}
