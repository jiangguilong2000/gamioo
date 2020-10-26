package io.gamioo.ioc;

import io.gamioo.ioc.factory.annotation.Autowired;
import io.gamioo.ioc.map.Command;
import io.gamioo.ioc.stereotype.Service;

import java.util.Map;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
@Service
public class DebugService {

    @Autowired
    private Map<String, Command> commandStore;


    public void handle(String value){
        Command command=commandStore.get(value);
        if(command!=null){
            command.execute();
        }
    }

}
