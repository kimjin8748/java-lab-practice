package BlackJackGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Blackjack {
    public static void main(String[] args) {
    	
    	int seed = Integer.parseInt(args[0]);
    	
    	// 덱 생성 및 셔플 (단 한 번 수행)
        Deck deck = new Deck();
        deck.shuffle(seed);
        
    	// 명령행 인자 처리 (Random Seed, Number of Players)
        if (args.length < 2) {
            System.out.println("시드와 플레이어 수를 정확히 입력하세요. ex)100 4");
            return;
        }

        int numPlayers = Integer.parseInt(args[1]);

        if (numPlayers < 1 || numPlayers > 5) {
            System.out.println("플레이어 수는 1~5명만 가능합니다.");
            return;
        }

        // 참여자 생성
        Player p1 = new Player("Player1"); // Interactive 모드
        List<Computer> computers = new ArrayList<>();
        for (int i = 2; i <= numPlayers; i++) {
            computers.add(new Computer("Player" + i));
        }
        House house = new House();

        // 초기 카드 분배 로직: 하나씩 순서대로 (P1 -> P2... -> House) 2회 반복
        for (int round = 0; round < 2; round++) {
            p1.addCard(deck.dealCard());
            for (Computer c : computers) {
                c.addCard(deck.dealCard());
            }
            house.addCard(deck.dealCard());
        }

        // House의 초기 합이 21인 경우 즉시 종료
        if (house.getScore() == 21) {
        	compareResults(p1, computers, house);
            return;
        }

        // 현재 상태 출력 (House 첫 카드는 HIDDEN)
        house.printInitialHand();
        p1.printHand();
        for (Computer c : computers) c.printHand();
        System.out.println("");

        // 플레이어 턴 진행
        Scanner scanner = new Scanner(System.in);
        p1.play(scanner, deck);

        // AI 플레이어들 진행
        for (Computer c : computers) {
            c.play(deck, seed); // seed를 활용해 확률 결정
        }

        // 하우스 턴 진행
        house.play(deck);

        // 최종 결과 출력
        compareResults(p1, computers, house);
    }

    private static void compareResults(Player p1, List<Computer> computers, House house) {
        System.out.println("\n--- Game Results ---");
        house.printHand();
        judge(p1, house);
        for (Computer c : computers) judge(c, house);
    }

    private static void judge(Hand player, House house) {
        int pScore = player.getScore();
        int hScore = house.getScore();

        // 플레이어 Bust인 경우 무조건 패배
        if (pScore > 21) {
            System.out.print("[Lose]  ");
            player.printHand();
        } 
        // 하우스 Bust 시 생존 플레이어 승리
        else if (hScore > 21) {
            System.out.print("[Win]  ");
            player.printHand();
        } 
        // 점수 비교
        else if (pScore > hScore) {
            System.out.print("[Win]  ");
            player.printHand();
        } else if (pScore < hScore) {
            System.out.print("[Lose]  ");
            player.printHand();
        } else {
            System.out.print("[Draw]  ");
            player.printHand();
        }
    }
}

// --- Supporting Classes ---

class Card {
    String suit;
    String rank;
    int value;

    public Card(int rankIdx, int suitIdx) {
        String[] suits = {"c", "h", "d", "s"}; // c: Clobber, h: Heart, d: Diamond, s: Spade
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        this.suit = suits[suitIdx];
        this.rank = ranks[rankIdx];
        
        // 점수 계산 규칙
        if (rankIdx == 0) this.value = 11; // Ace 기본 11
        else if (rankIdx >= 9) this.value = 10; // 10, J, Q, K는 10
        else this.value = rankIdx + 1;
    }

    @Override
    public String toString() { return rank + suit; }
}

class Deck {
    private Card[] deck = new Card[52];
    private int cardsUsed = 0;

    public Deck() {
        int i = 0;
        for (int s = 0; s < 4; s++) {
            for (int r = 0; r < 13; r++) {
                deck[i++] = new Card(r, s);
            }
        }
    }

    public void shuffle(int seed) {
        Random random = new Random(seed);
        for (int i = deck.length - 1; i > 0; i--) {
            int rand = random.nextInt(i + 1);
            Card temp = deck[i];
            deck[i] = deck[rand];
            deck[rand] = temp;
        }
        cardsUsed = 0;
    }

    public Card dealCard() {
        if (cardsUsed == deck.length) throw new IllegalStateException("No cards are left in the deck.");
        cardsUsed++;
        return deck[cardsUsed - 1];
    }
}

class Hand {
    List<Card> cards = new ArrayList<>();
    String name;

    public void addCard(Card c) { cards.add(c); }

    // Ace 계산 로직 (기본 11, 초과 시 1로 변환)
    public int getScore() {
        int total = 0;
        int aceCount = 0;
        for (Card c : cards) {
            total += c.value;
            if (c.rank.equals("A")) aceCount++;
        }
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }
        return total;
    }

    public void printHand() {
    	StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            sb.append(cards.get(i));
            if (i < cards.size() - 1) sb.append(", ");
        }
        if(getScore() <= 21) {
        	System.out.println(name + ": " + sb.toString() + " (" + getScore() + ")");
        } else {
        	System.out.println(name + ": " + sb.toString() + " (" + getScore() + ")" + " - Bust!");
        }
    }
}

class Player extends Hand {
    public Player(String name) { this.name = name; }

    public void play(Scanner sc, Deck deck) {
    	System.out.println("--- " + name + " turn ---");
    	printHand();
        while (getScore() <= 21) {
            // 사용자 입력 모드
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("Hit")) {
                addCard(deck.dealCard());
                printHand();
            } else if (input.equalsIgnoreCase("Stand")) {
            	printHand();
                break;
            }
        }
    }
}

class Computer extends Hand {
    public Computer(String name) { this.name = name; }

    public void play(Deck deck, int seed) {
        Random random = new Random(); // 확률 결정을 위한 새 인스턴스
        System.out.println("\n--- " + name + " turn ---");
        printHand();
        while (getScore() <= 21) {
            int score = getScore();
            // AI 규칙
            if (score < 14) {
            	System.out.println("Hit");
                addCard(deck.dealCard());
                printHand();
            } else if (score >= 14 && score <= 17) {
                if (random.nextInt(2) == 1) {
                	System.out.println("Hit");
                	addCard(deck.dealCard()); // 1/2 확률
                	printHand();
                } else {
                	System.out.println("Stand");
                	printHand();
                	break;
                }
            } else {
            	System.out.println("Stand");
            	printHand();
                break;
            }
        }
    }
}

class House extends Hand {
    public House() { this.name = "House"; }

    // 초기 상태에서는 첫 번째 카드를 HIDDEN 처리
    public void printInitialHand() {
        System.out.println("House: HIDDEN, " + cards.get(1));
    }

    public void play(Deck deck) {
        // 하우스 규칙: 16 이하 Hit, 17 이상 Stand
    	System.out.println("\n--- House turn ---");
    	printHand();
        while (getScore() <= 16) {
        	System.out.println("Hit");
            addCard(deck.dealCard());
            printHand();
        } 
        if (getScore() <= 21) {
        	System.out.println("Stand");
        	printHand();
        }
    }
}