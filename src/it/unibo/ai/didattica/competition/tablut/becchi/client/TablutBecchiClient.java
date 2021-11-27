package it.unibo.ai.didattica.competition.tablut.becchi.client;

import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.becchi.player.PlayerBecco;
import it.unibo.ai.didattica.competition.tablut.becchi.player.PlayerBeccoBlack;
import it.unibo.ai.didattica.competition.tablut.becchi.player.PlayerBeccoWhite;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.*;

import java.io.IOException;
import java.net.UnknownHostException;

public class TablutBecchiClient extends TablutClient {

    private final PlayerBecco becco;

    public TablutBecchiClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);

        GameBecchiTablut game = new GameBecchiTablut(0, 0, "garbage", "client_w", "client_b");

        if (this.getPlayer().equals(State.Turn.WHITE)) {
            becco = new PlayerBeccoWhite(timeout, game);
        } else {
            becco = new PlayerBeccoBlack(timeout, game);
        }
    }

    public TablutBecchiClient(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
        this(player, "Becchi", timeout, ipAddress);
    }

    public TablutBecchiClient(String player) throws UnknownHostException, IOException {
        this(player, 60, "localhost");
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        String role = "";
        String name = "Becchi";
        String ipAddress = "localhost";
        int timeout = 60;
        // TODO: change the behavior?
        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            System.out.println("Selected client: " + args[0]);
            role = (args[0]);
        }
        if (args.length >= 2) {
            timeout = Integer.parseInt(args[1]);
        }
        if (args.length == 3) {
            ipAddress = args[2];
        }

        System.out.println("Timeout: " + timeout + "s");

        TablutBecchiClient client = new TablutBecchiClient(role, name, timeout-1, ipAddress);
        client.run();
    }

    private void printBecchiSignature() {
        System.out.println("Noi siamo i Vincitori Becchi");
    }

    private void sendActionToServer(Action a) {
        try {
            this.write(a);
            System.out.println("Action sent to server.");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        State state = new StateTablut();
        state.setTurn(State.Turn.WHITE);
        printBecchiSignature();
        System.out.println("Ashton Tablut game");

        System.out.println(this.getName() + ", you are player " + this.getPlayer().toString() + "!");

        while(true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(1);
            }
            System.out.println("\nThe current state is:");
            state = this.getCurrentState();
            System.out.println(state.toString());

            if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                if (this.getPlayer().equals(State.Turn.BLACK)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                }
                System.out.println("YOU LOSE!");
                System.exit(0);
            }

            if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                if (this.getPlayer().equals(State.Turn.WHITE)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                }
                System.out.println("YOU LOSE!");
                System.exit(0);
            }

            if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                System.out.println("DRAW!");
                System.exit(0);
            }

            if (this.getPlayer().equals(this.getCurrentState().getTurn())) {
                try {
                    System.out.println("Your turn");
                    sendActionToServer(becco.getOptimalAction(state, true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Waiting for your opponent move... ");
            }
        }
    }
}
