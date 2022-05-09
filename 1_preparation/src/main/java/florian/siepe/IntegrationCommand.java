package florian.siepe;

import florian.siepe.entity.dto.lobby.LobbyRegisterSearchResponse;
import florian.siepe.entity.dto.trading.TradingRegisterEntry;
import florian.siepe.io.JsonLineTextReader;
import florian.siepe.io.JsonTextReader;
import org.slf4j.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;


@Command(name = "integrate", mixinStandardHelpOptions = true)
public class IntegrationCommand implements Runnable {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IntegrationCommand.class);

    @Parameters(paramLabel = "<lobbyRegister>", description = "The file dump of the lobby register")
    File lobbyRegister;
    @Parameters(paramLabel = "<tradingRegister>", description = "The file dump of the trading register")
    File tradingRegister;

    @Override
    public void run() {
        final var tradingRegisterReader = new JsonLineTextReader<>(TradingRegisterEntry.class);
        final var tradingRegisterEntries = tradingRegisterReader.read(tradingRegister);

        final var lobbyRegisterReader = new JsonTextReader<>(LobbyRegisterSearchResponse.class);
        final var lobbyRegisterData = lobbyRegisterReader.read(lobbyRegister);

        logger.info("Got {} entries from the lobby register", lobbyRegisterData.results.size());
        logger.info("Got {} entries from the trading register", tradingRegisterEntries.size());
    }
}
