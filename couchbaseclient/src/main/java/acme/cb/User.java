package acme.cb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@ToString
public class User {

    String id;
    String name;
    String address;
    String [] interests;

    public static User of(String id, String name, String address, String [] interests) {
        return new User(id, name, address, Arrays.copyOf(interests, interests.length));
    }
}
