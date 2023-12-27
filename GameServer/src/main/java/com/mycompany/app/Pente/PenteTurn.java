
package main.java.com.mycompany.app.Pente;

public class PenteTurn {
    // required
    private int posX;
    private int posY;
    private int playerNumber;

    // optional
    private boolean isTurnOneAction;

    private PenteTurn(PenteTurnBuilder builder) {
        this.posX = builder.posX;
        this.posY = builder.posY;
        this.playerNumber = builder.playerNumber;
        this.isTurnOneAction = builder.isTurnOneAction;

    } 

    public static class PenteTurnBuilder {
        // required
        private int posX;
        private int posY;
        private int playerNumber;

        // optional
        private boolean isTurnOneAction;

        public PenteTurnBuilder(int posX, int posY, int playerNumber) {
            this.posX = posX;
            this.posY = posY;
            this.playerNumber = playerNumber;
        }

        public void setTurnOneAction(boolean isTurnOneAction) {
            this.isTurnOneAction = isTurnOneAction;
        }

        public PenteTurn build() {
            return new PenteTurn(this);
        }
    }
}