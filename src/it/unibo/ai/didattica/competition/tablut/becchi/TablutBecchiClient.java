package it.unibo.ai.didattica.competition.tablut.becchi;

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

        if (player.toLowerCase().equals("white")) {
            becco = new PlayerBeccoWhite();
        } else if (player.toLowerCase().equals("black")) {
            becco = new PlayerBeccoBlack();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public TablutBecchiClient(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
        this(player, "Becchi", timeout, ipAddress);
    }

    public TablutBecchiClient(String player) throws UnknownHostException, IOException {
        this(player, "Becchi", 60, "localhost");
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {
        String role = "";
        String name = "random";
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
        //FIXME add Thread.sleep(timeout-3);
    }

    private void sendActionToServer(String from, String to, StateTablut.Turn turn) {
        Action a;
        try {
            a = new Action(from, to, State.Turn.WHITE);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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

            if (this.getPlayer().equals(State.Turn.WHITE)) {
                // TODO: fix this logic, this is a bit shit
                // Mio turno
                if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
                    // TODO: implementation of minimax search algoritm
                    Action a = null;
                    try {
                        a = becco.getOptimalAction(state);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // Turno dell'avversario
                else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
                    System.out.println("Waiting for your opponent move... ");
                }
                // ho vinto
                else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                }
                // ho perso
                else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                }
                // pareggio
                else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }
            } else {
                // Mio turno
                if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
                    // TODO: implementation of minimax search algoritm
                    Action a = null;
                    try {
                        a = becco.getOptimalAction(state);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
                    System.out.println("Waiting for your opponent move... ");
                } else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                } else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                } else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }
            }
        }
    }
}
