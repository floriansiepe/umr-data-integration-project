package florian.siepe;

import florian.siepe.entity.dto.lobby.LobbyRegisterSearchResponse;
import florian.siepe.entity.dto.trading.TradingRegisterEntry;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import javax.json.bind.JsonbBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;

@Command(name = "integrate", mixinStandardHelpOptions = true)
public class IntegrationCommand implements Runnable {

    @Parameters(paramLabel = "<lobbyRegister>", description = "The file dump of the lobby register")
    File lobbyRegister;
    @Parameters(paramLabel = "<tradingRegister>", description = "The file dump of the trading register")
    File tradingRegister;

    @Override
    public void run() {
        final var jsonb = JsonbBuilder.create();
        try {
            final var lobbyEntryList = Files.readAllLines(lobbyRegister.toPath(), StandardCharsets.UTF_8);
            if (!lobbyEntryList.isEmpty()) {
                final var lobbyRegisterSearchResponse = jsonb.fromJson(lobbyEntryList.get(0), LobbyRegisterSearchResponse.class);
                System.out.println(lobbyRegisterSearchResponse.results.size());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final var tradingRegisterEntries = new LinkedList<TradingRegisterEntry>();
        try (BufferedReader br = new BufferedReader(new FileReader(tradingRegister))) {
            for (String line; (line = br.readLine()) != null; ) {
                // process the line.
                final var tradingRegisterEntry = jsonb.fromJson(line, TradingRegisterEntry.class);
                tradingRegisterEntries.add(tradingRegisterEntry);
            }
            // line is not visible here.
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(tradingRegisterEntries.size());

    }
}
