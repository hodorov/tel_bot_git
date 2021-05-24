package github.smartsoft.telegrambot.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "access_request")
public class AccessRequest extends BaseRequest {
}
