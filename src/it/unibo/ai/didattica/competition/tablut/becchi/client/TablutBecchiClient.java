package it.unibo.ai.didattica.competition.tablut.becchi.client;

import it.unibo.ai.didattica.competition.tablut.becchi.domain.GameBecchiTablut;
import it.unibo.ai.didattica.competition.tablut.becchi.player.PlayerBecco;
import it.unibo.ai.didattica.competition.tablut.becchi.player.PlayerBeccoBlack;
import it.unibo.ai.didattica.competition.tablut.becchi.player.PlayerBeccoWhite;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TablutBecchiClient extends TablutClient {

    private final PlayerBecco becco;

    public TablutBecchiClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, name, timeout, ipAddress);

        GameBecchiTablut game = new GameBecchiTablut();

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
        this(player, "Becchi", 60, "localhost");
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        String role = "";
        String name = "Il miglior Becco, col pelo lungo, le corna belle";
        String ipAddress = "localhost";
        int timeout = 60;
        // TODO: change the behavior?
        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            System.out.println(args[0]);
            role = (args[0]);
        }
        if (args.length == 2) {
            System.out.println(args[1]);
            timeout = Integer.parseInt(args[1]);
        }
        if (args.length == 3) {
            ipAddress = args[2];
        }
        System.out.println("Selected client: " + args[0]);

        TablutBecchiClient client = new TablutBecchiClient(role, name, timeout, ipAddress);
        client.run();
        // sleep
        // kill client retrieving the move
    }

    private void sendActionToServer(Action a) {
        System.out.println("Mossa scelta: " + a.toString());
        try {
            this.write(a);
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
        Game rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
        System.out.println("Ashton Tablut game");

        List<int[]> pawns = new ArrayList<int[]>();
        List<int[]> empty = new ArrayList<int[]>();

        System.out.println("You are player " + this.getPlayer().toString() + "!");

        while(true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(1);
            }
            System.out.println("Current state:");
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
                    sendActionToServer(becco.getOptimalAction(state));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Waiting for your opponent move... ");
            }
        }
    }
}
