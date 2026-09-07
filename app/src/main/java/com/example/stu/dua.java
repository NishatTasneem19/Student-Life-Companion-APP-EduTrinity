package com.example.stu;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class dua extends AppCompatActivity {

    Button b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15,
            b16, b17, b18, b19, b20, b21, b22, b23, b24, b25, b26;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dua);

        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);
        b7 = findViewById(R.id.b7);
        b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9);
        b10 = findViewById(R.id.b10);
        b11 = findViewById(R.id.b11);
        b12 = findViewById(R.id.b12);
        b13 = findViewById(R.id.b13);
        b14 = findViewById(R.id.b14);
        b15 = findViewById(R.id.b15);
        b16 = findViewById(R.id.b16);
        b17 = findViewById(R.id.b17);
        b18 = findViewById(R.id.b18);
        b19 = findViewById(R.id.b19);
        b20 = findViewById(R.id.b20);
        b21 = findViewById(R.id.b21);
        b22 = findViewById(R.id.b22);
        b23 = findViewById(R.id.b23);
        b24 = findViewById(R.id.b24);
        b25 = findViewById(R.id.b25);
        b26 = findViewById(R.id.b26);

        b1.setOnClickListener(v -> showDua("Dua to extinguish anger",
                "اللَّهُمَّ أَذْهِبْ غَيْظَ قَلْبِي\n\nOh Allah, remove anger from my heart.\n\n."));

        b2.setOnClickListener(v -> showDua("Dua for relief from sadness",
                "اللهمّ فارج الهم، كاشف الغم، مذهب الحزن، اكشف اللهمّ عنّي همّي وغمّي ، وأذهب عنّي حزنيِِ\n\nOh Allah, Reliever of anxiety, Remover of distress, Dispeller of grief! Remove my anxiety, distress, and dispel from me my sadness.\n\n....") );

        b3.setOnClickListener(v -> showDua("Dua to ease your destiny",
                "اللهم إن كان هذا الأمر خيرا لي فَاقْدُرْهُ لِي وَيَسِّرْهُ لِي ثُمَّ بَارِكْ لِي فِيهِ\n\nOh Allah, if my intended action is best for me, make it destined and easy for me, and grant me Your Blessings in it.\n\n..."));

        b4.setOnClickListener(v -> showDua("Dua for purifying the heart",
                "اللهم طهر قلبي من كل سوء ، اللهم طهر قلبي من كل ما يبغضك، اللهم طهر قلبي من كل غلٍ وحقدٍ وحسد وكبر\n\nOh Allah, clean away all forms of evil from my heart. Oh Allah, clean my heart and remove everything that displeases you. Oh Allah, clean my heart of every form of bitterness, hard feelings, and jealousy.\n\n..."));

        b5.setOnClickListener(v -> showDua("Dua for relief from anxiety",
                "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ الْهَمِّ وَالْحُزْنِ وَالْعَجْزِ وَالْكَسَلِ وَالْبُخْلِ وَالْجُبْنِ وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ\n\nO Allah, I take refuge in you from anxiety and sorrow, weakness and laziness, miserliness and cowardice, the burden of debts and from being overpowered by men.\n\n..."));

        b6.setOnClickListener(v -> showDua("Dua to remove confusion from one's heart",
                "اللهمّ اجعل في قلبي نوراً، وفي لساني نوراً، واجعل في سمعي نوراً، واجعل في بصري نوراً، واجعل من خلفي نوراً، ومن أمامي نوراً، واجعل من فوقي نوراً، ومن تحتي نوراً، اللهمّ أعطني نوراً\n\nOh Allah! Place in my heart, light. Place in my tongue, light. Place in my hearing, light. Place in my sight, light. Place behind me, light. Place before me, light. Place above me, light. Place under me, light. Oh Allah grant me light!\n\n..."));

        b7.setOnClickListener(v -> showDua("Dua for protection from one's nafs",
                "اللهم إني أعوذ بك من شر سمعي، ومن شر بصري، ومن شر لساني، ومن شر قلبي، ومن شر منيي\n\nOh Allah, I seek protection in you from the evil of my hearing, from the evil of my sight, from the evil of my tongue, from the evil of my heart, and from the evil of myself.\n\n..."));

        b8.setOnClickListener(v -> showDua("Dua to dispel the gloom of depression",
                "اللهم اخرجني من الظلمات إلى النور\n\nOh Allah take me out of darkness and into the light.\n\n..."));

        b9.setOnClickListener(v -> showDua("Dua for optimism in times of adversity",
                "اللهم إليك أشكو ضعف قوتي وقلة حيلتي وهواني على الناس يا أرحم الراحمين أنت ربُّ المستضعفين وانت ربّي\n\nTo You, my Lord, I complain of my weakness, lack of support and the humiliation I am made to receive. Most Compassionate and Merciful! You are the Lord of the weak, and you are my Lord.\n\n..."));

        b10.setOnClickListener(v -> showDua("Dua for courage",
                "اللهم امنحني القوة لأقاوم نفسي، والشجاعة لأواجه ضعفي، واليقين لأتقبل قدري، والرضا ليرتاح عقلي، والفهم ليطمئن قلبي\n\nOh Allah! Grant me the strength to oppose myself, the courage to face my weakness, the conviction to accept my faith, the satisfaction of to relax my mind, and the understanding to reassure my heart.\n\n..."));

        b11.setOnClickListener(v -> showDua("Dua for when one's Imaan feels low",
                "اللهم املأ قلبي بحبك\n\nOh Allah fill my heart with your love.\n\n."));

        b12.setOnClickListener(v -> showDua("Dua for when one feels doubtful",
                "اللهم إن كان هذا الأمر خيرا لي فَاقْدُرْهُ لِي وَيَسِّرْهُ لِي ثُمَّ بَارِكْ لِي فِيهِ\n\nOh Allah, if my intended action is best for me, make it destined and easy for me, and grant me Your Blessings in it.\n\n..."));

        b13.setOnClickListener(v -> showDua("Dua for gratitude",
                "اللهم إن شكرك نعمة، تستحق الشكر، فعلّمني كيف أشكرك ، الحمد لله كما ينبغى لجلال وجهك وعظيم سلطانك\n\nOh Allah thanking you is a blessing, you deserve all thankfulness. All praise is due to Allah the way He should his magnificence deserves to be praised.\n\n..."));

        b14.setOnClickListener(v -> showDua("Dua for happiness",
                "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ زَوَالِ نِعْمَتِكَ، وَتَحَوُّلِ عَافِيَتِكَ، وَفُجَاءَةِ نِقْمَتِكَ، وَجَمِيعِ سَخَطِكَ\n\nO Allah! I seek refuge in You from the decline of Your blessings, the passing of safety, the sudden onset of Your punishment and from all that displeases you.\n\n..."));

        b15.setOnClickListener(v -> showDua("Dua to remove guilt from the heart",
                "يارب امسح على صدري برحمتك\n\nOh Lord, wipe my chest clean with your mercy.\n\n."));
        b16.setOnClickListener(v -> showDua("Dua to dispel hatred",
                "اللهم لا تجعل في قلبي كراهية لأحد\n\nOh Allah, don't let the hate of anyone reside in my heart.\n\n."));

        b17.setOnClickListener(v -> showDua("Dua for guidance",
                "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ\n\nOur Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy. Indeed, You are the Bestower.\n\n..."));

        b18.setOnClickListener(v -> showDua("Dua to ask for paradise",
                "أشهد أن لا إله إلا الله ، نستغفِرُ الله ، نسألُك الجنةَ ونعوذُ بك من النار\n\nI testify that there is nothing worthy of worship other than Allah and we seek the forgiveness of Allah. We ask You for Paradise and take refuge in You from the Fire.\n\n..."));

        b19.setOnClickListener(v -> showDua("Dua for repentance",
                "لا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ\n\nThere is no deity but You. Glory be to You! Verily, I have been among the wrongdoers.\n\n."));

        b20.setOnClickListener(v -> showDua("For protection from difficulty",
                "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنْ الْهَمِّ وَالْحُزْنِ وَالْعَجْزِ وَالْكَسَلِ وَالْبُخْلِ وَالْجُبْنِ وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ\n\nO Allah, I take refuge in You from anxiety and sorrow, weakness and laziness, miserliness and cowardice, the burden of debts and from being overpowered by men.\n\n..."));

        b21.setOnClickListener(v -> showDua("Dua when one feels lonely",
                "اللهُمَّ رَحْمَتَكَ أرجُو، فَلا تَكِلْنِي إلى نَفْسي طَرْفَةَ عَيْنٍ، وأصْلِحْ لي شَأني كُلَّهُ، لا إله إلا أنْتَ\n\nO Allah, it is Your mercy that I hope for, so do not leave me in charge of my affairs even for a blink of an eye, and rectify for me all of my affairs. None has the right to be worshiped except You.\n" +
                        "\n\n\n..."));

        b22.setOnClickListener(v -> showDua("Dua for confidence",
                "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِّن لِّسَانِي يَفْقَهُوا قَوْلِي\n\nOh lord, expand my chest, ease my affair, and untie the knot in my tongue and perfect my expression.\n\n..."));

        b23.setOnClickListener(v -> showDua("Dua for Allah's love",
                "،اللهم إني أسألك حبك، وحب من يحبك، والعمل الذي يبلغني حبك\n" +
                        "اللهم اجعل حبك أحب إليّ من نفسي، وأهلي، ومن الماء البارد\n\nO Allah! I ask You [to grant me] Your Love, the love of those who love You, and deeds which will cause me to earn Your Love. O Allah! Make Your Love dearer to me than [the love of] myself, my family and the cold water [winter].- At-Tirmidhi.\n\n..."));

        b24.setOnClickListener(v -> showDua("Dua for the Hereafter",
                "ِ\n\nO Lord, give us in this world that which is good and in the Hereafter that which is good, and save us from the punishment of the Fire.\n\nRabbana atina fid-dunya hasanatan..."));

        b25.setOnClickListener(v -> showDua("Dua for knowledge",
                "\n\nO Lord, give us in this world that which is good and in the Hereafter that which is good, and save us from the punishment of the Fire.\n\nAllahumma inni as'aluka 'Ilman naafi'an..."));

        b26.setOnClickListener(v -> showDua("Dua to unlock your potential",
                "اللهم اجعلني أرى المواهب و نقاط قوت الذين وضعته في نفسي\n\nOh Allah, make me see the talents and strengths you have put inside of me.\n\n..."));

    }

    private void showDua(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}