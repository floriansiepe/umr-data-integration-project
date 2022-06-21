package florian.siepe;

import florian.siepe.control.DataService;
import florian.siepe.control.Deduplication;
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
    private final Deduplication deduplication;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(IntegrationCommand.class);

    @Parameters(paramLabel = "<lobbyRegister>", description = "The file dump of the lobby register", defaultValue = "lobby-data-2022-05-08.json")
    File lobbyRegister;
    @Parameters(paramLabel = "<tradingRegister>", description = "The file dump of the trading register", defaultValue = "corporate-events-dump.json")
    File tradingRegister;

    public IntegrationCommand(final DataService dataService, final Deduplication deduplication) {
        this.dataService = dataService;
        this.deduplication = deduplication;
    }

    @Override
    public void run() {
        final var tradingRegisterReader = new JsonLineTextReader<>(TradingRegisterEntry.class);
        final var tradingRegisterEntries = tradingRegisterReader.read(tradingRegister);

        final var lobbyRegisterReader = new JsonTextReader<>(LobbyRegisterSearchResponse.class);
        final var lobbyRegisterData = lobbyRegisterReader.read(lobbyRegister);

        dataService.insertLobbyRegisterData(lobbyRegisterData);
        dataService.insertTradingRegisterData(tradingRegisterEntries);
        logger.info("Got {} entries from the lobby register", lobbyRegisterData.results.size());
        logger.info("Got {} entries from the trading register", tradingRegisterEntries.size());

        deduplication.deduplicatePersons();

        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
