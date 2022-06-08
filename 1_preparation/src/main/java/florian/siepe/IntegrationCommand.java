package florian.siepe;

import florian.siepe.control.DataService;
import florian.siepe.entity.dto.lobby.search.LobbyRegisterSearchResponse;
import florian.siepe.entity.dto.trading.TradingRegisterEntry;
import florian.siepe.io.JsonLineTextReader;
import florian.siepe.io.JsonTextReader;
import org.slf4j.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import javax.inject.Singleton;
import java.io.File;


@Command(name = "integrate", mixinStandardHelpOptions = true)
@Singleton
public class IntegrationCommand implements Runnable {
    private final DataService dataService;

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IntegrationCommand.class);

    @Parameters(paramLabel = "<lobbyRegister>", description = "The file dump of the lobby register", defaultValue = "lobby-data-2022-05-08.json")
    File lobbyRegister;
    @Parameters(paramLabel = "<tradingRegister>", description = "The file dump of the trading register", defaultValue = "corporate-events-dump.json")
    File tradingRegister;

    public IntegrationCommand(final DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public void run() {
        dataService.createDb();
        final var tradingRegisterReader = new JsonLineTextReader<>(TradingRegisterEntry.class);
        final var tradingRegisterEntries = tradingRegisterReader.read(tradingRegister);

        final var lobbyRegisterReader = new JsonTextReader<>(LobbyRegisterSearchResponse.class);
        final var lobbyRegisterData = lobbyRegisterReader.read(lobbyRegister);

        //dataService.insertLobbyRegisterData(lobbyRegisterData);
        dataService.insertTradingRegisterData(tradingRegisterEntries);
        logger.info("Got {} entries from the lobby register", lobbyRegisterData.results.size());
        logger.info("Got {} entries from the trading register", tradingRegisterEntries.size());

        try {
            Thread.sleep(60000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
