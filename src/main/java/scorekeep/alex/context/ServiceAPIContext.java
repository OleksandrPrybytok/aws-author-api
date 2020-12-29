package scorekeep.alex.context;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Alexander Pribytok
 * Date: 17.12.2020.
 */
@Data
@AllArgsConstructor
public class ServiceAPIContext {

    private final Caller caller;
}
