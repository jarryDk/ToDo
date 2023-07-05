package dk.jarry.todo.control;

import jakarta.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title="ToDos Api",
        version = "1.1.0",
        license = @License(
            name = "Apache 2.0",
            url = "http://www.apache.org/licenses/LICENSE-2.0.html"
        ),
        contact = @Contact(
            name = "Michael Bornholdt Nielsen",
            email = "michaelbornholdtnielsen@gmail.com"
        ),
        description = "Simple ToDo app"
    ),
    servers = @Server(url="https://todo.jarry.dk:8443")
)
public class TodoApplication extends Application {

}
