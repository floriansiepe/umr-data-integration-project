package florian.siepe.clients;

import florian.siepe.entity.dto.lobby.detail.LobbyRegisterDetailResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@RegisterRestClient(baseUri = "https://www.lobbyregister.bundestag.de")
public interface LobbyRegisterClient {
    @GET
    @Path("sucheJson/{id1}/{id2}")
    LobbyRegisterDetailResponse getDetails(@PathParam("id1") String id1, @PathParam("id2") String id2);
}
