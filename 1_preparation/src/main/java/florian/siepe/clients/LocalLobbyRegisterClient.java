package florian.siepe.clients;

import florian.siepe.entity.dto.lobby.detail.LobbyRegisterDetailResponse;
import florian.siepe.io.JsonTextReader;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;

@ApplicationScoped
public class LocalLobbyRegisterClient implements LobbyRegisterClient {

    JsonTextReader<LobbyRegisterDetailResponse> reader = new JsonTextReader<>(LobbyRegisterDetailResponse.class);

    @Override
    public LobbyRegisterDetailResponse getDetails(final String registerEntry, final String registerEntryId) {
        final var file = new File("registerEntries/" + String.format("%s-%s", registerEntry, registerEntryId));
        return reader.read(file);
    }
}
