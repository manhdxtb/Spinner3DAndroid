package com.app.spinner.activity.battle;

public class BattleData {
    public static String shapeYou;
    public static int colorYou;
    
    public static String shapeP2;
    public static int colorP2;
    public static String nameP2 = "Player 2";
    
    public static float rpmYou;
    public static float rpmP2;
    
    public static boolean youWin;
    public static float finalSpeed;

    private static final String[] NAMES_P2 = {
            "Minh Huy", "Nam Anh", "Tuan Kiet", "Linh Chi", "Hoang Long", "Thanh Thao", "Duc Duy", "Phuong Vy", // VN
            "Somchai Lee", "Mali Wan", "Arthit Ray", "Kanya Siri", "Somsak Dee", "Ploy Pailin", "Chai Son", "Anong Mon", // Thai
            "Aarav Raj", "Vihaan Dev", "Arjun Das", "Ananya Sri", "Diya Sharma", "Rohan Gupta", "Ishaan Roy", "Saanvi Rao", // India
            "James Bond", "John Smith", "Robert Dow", "Mary Jane", "Linda May", "David Cook", "Mike Ross", "Lisa Ann", // USA
            "Wei Wang", "Hao Chen", "Yan Li", "Fang Liu", "Chen Zhang", "Li Yang", "Ming Huang", "Lei Zhao", // China
            "Juan Dela", "Jose Rizal", "Maria Clara", "Joshua Cruz", "Angel Locs", "Rico Yan", "Pedro Pend", "Lito Lapid", // Philippines
            "Ahmet Can", "Mehmet Ali", "Mustafa Koc", "Fatma Nur", "Ayse Gul", "Emre Can", "Burak Oz", "Elif Su", // Turkey
            "Lucas Silv", "Gabriel San", "Matheus Fer", "Julia Lind", "Beatriz San", "Leo Paul", "Bruno Dias", "Alice Fer", // Brazil
            "Aditya Put", "Bagus Set", "Putri Ayu", "Siti Nur", "Budi Sant", "Agus Sus", "Dewi Sart", "Rizky Ram", // Indonesia
            "Juan Carl", "Jose Luis", "Maria Fern", "Angel Gabri", "Luis Alfon", "Carlos Ed", "Ana Mari", "Rosa Isab", // Mexico
            "Ahmad Kam", "Mohd Faiz", "Abdul Rah", "Nurul Ain", "Siti Fat", "Lee Min", "Tan Wei", "Wong Ken", // Malaysia
            "Hans Wolf", "Karl Hein", "Otto Schm", "Emma Mull", "Anna Beck", "Max Web", "Paul Fis", "Erik Wag", // Germany
            "Jean Luc", "Paul Bert", "Marc Oliv", "Marie Lou", "Anne Clar", "Leo Marc", "Eric Phil", "René Guy", // France
            "Juan Jose", "Jose Mari", "Luis Migu", "Ana Isab", "Rosa Mari", "Carl Jorg", "Manu Garr", "Paco Fern", // Spain
            "Luis Dieg", "Paco Rap", "Luz Mari", "Ana Paz" // Extra to reach 116
    };

    public static void randomizeNameP2() {
        nameP2 = NAMES_P2[new java.util.Random().nextInt(NAMES_P2.length)];
    }
}
