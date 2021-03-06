package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.songs.Pitch;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class PitchTest
    extends TestCase
{

    @Test
    public void testPitch() {
        if (false
            || true
                ) {
            //  print table
            for (Pitch p : Pitch.values()) {
                StringBuilder sb = new StringBuilder();
                sb.append(p.name());
                while (sb.length() < 4)
                    sb.append(" ");
                if (p.getNumber() <= 9)
                    sb.append(" ");
                sb.append(p.getNumber());
                sb.append(" ");
                sb.append(p.getFrequency());
                System.out.println(sb.toString());
            }
        }
        assertEquals(Pitch.A1.getFrequency(), 55.0, 1e-20);
        assertEquals(Pitch.E1.getFrequency(), 41.2034, 1e-4);
        assertEquals(Pitch.A2.getFrequency(), 110.0, 1e-20);
        assertEquals(Pitch.E2.getFrequency(), Pitch.E1.getFrequency() * 2, 1e-20);
        assertEquals(Pitch.A3.getFrequency(), 220.0, 1e-20);
        assertEquals(Pitch.E3.getFrequency(), Pitch.E1.getFrequency() * 4, 1e-12);
        assertEquals(Pitch.A4.getFrequency(), 440.0, 1e-20);
        assertEquals(Pitch.E4.getFrequency(), Pitch.E1.getFrequency() * 8, 1e-12);
        assertEquals(Pitch.A5.getFrequency(), 880.0, 1e-20);
        assertEquals(Pitch.C6.getFrequency(), 1046.5022612023945, 1e-20);// human voice, saprano
        assertEquals(Pitch.C8.getFrequency(), 4186.009044809578, 1e-20);//  piano
    }

    @Test
    public void testIsSharp() {

        int sharpCount = 0;
        int naturalCount = 0;
        int flatCount = 0;
        for (Pitch p : Pitch.values()) {
            if (p.isSharp())
                sharpCount++;
            if (p.isFlat())
                flatCount++;
            if (p.isSharp() && !p.isFlat())
                naturalCount++;
        }
        assertEquals(7 * 7 + 2, sharpCount);
        assertEquals(7 * 7 + 2, flatCount);
        assertEquals(7 * 7 + 2, naturalCount);

        Pitch p = Pitch.A0;
        assertEquals(false,p.isSharp());
        assertEquals(false,p.isFlat());
        assertEquals(true,p.isNatural());
        int sharps = 0;
        int naturals = 1;
        int flats = 0;
        for (int i = 0; i < Pitch.values().length; i++)  //  safety only
        {
            //System.out.println(p.toString());
            p = p.offsetByHalfSteps(1);
            if (p == null)
                break;
            if (p.isSharp()) sharps++;
            if (p.isNatural()) naturals++;
            if (p.isFlat()) flats++;
        }
        assertEquals(0, sharps);
        assertEquals(52, naturals);
        assertEquals(36, flats);

        p = Pitch.As0;
        assertEquals(true,p.isSharp());
        assertEquals(false,p.isFlat());
        assertEquals(false,p.isNatural());

         sharps = 0;
         naturals = 0;
         flats = 0;
        for (int i = 0; i < Pitch.values().length; i++)  //  safety only
        {
            p = Pitch.values()[i];
            //System.out.println(p.toString());
            p = p.offsetByHalfSteps(2);
            if (p == null)
                break;
            if (p.isSharp()) sharps++;
            if (p.isNatural()) naturals++;
            if (p.isFlat()) flats++;
        }
        assertEquals(21, sharps);
        assertEquals(80, naturals);
        assertEquals(49, flats);
    }


    @Test
    public void testfromFrequency() {

        assertEquals(Pitch.A0, Pitch.findPitchFromFrequency(1) );
        assertEquals(Pitch.E1, Pitch.findPitchFromFrequency(41));
        assertEquals(Pitch.E1, Pitch.findPitchFromFrequency(42));
        assertEquals(Pitch.Es1, Pitch.findPitchFromFrequency(43));
        assertEquals(Pitch.A5, Pitch.findPitchFromFrequency(880));
        assertEquals(Pitch.A5, Pitch.findPitchFromFrequency(906));
        assertEquals(Pitch.As5, Pitch.findPitchFromFrequency(907));
        assertEquals(Pitch.Bb5, Pitch.findPitchFromFrequency(933)); //  fixme: improve the response
        assertEquals(Pitch.C8, Pitch.findPitchFromFrequency(7030));
    }
}
/*
A0   0 27.5
As0  1 29.13523509488062
Bb0  1 29.13523509488062
B0   2 30.86770632850775
Bs0  3 32.70319566257483
Cb1  2 30.86770632850775
C1   3 32.70319566257483
Cs1  4 34.64782887210901
Db1  4 34.64782887210901
D1   5 36.70809598967594
Ds1  6 38.890872965260115
Eb1  6 38.890872965260115
E1   7 41.20344461410875          //    bass low E
Es1  8 43.653528929125486
Fb1  7 41.20344461410875
F1   8 43.653528929125486
Fs1  9 46.2493028389543
Gb1  9 46.2493028389543
G1  10 48.999429497718666
Gs1 11 51.91308719749314
Ab1 11 51.91308719749314
A1  12 55.0                       //    bass open A
As1 13 58.27047018976124
Bb1 13 58.27047018976124
B1  14 61.7354126570155
Bs1 15 65.40639132514966
Cb2 14 61.7354126570155
C2  15 65.40639132514966
Cs2 16 69.29565774421802
Db2 16 69.29565774421802
D2  17 73.41619197935188          //    bass open D
Ds2 18 77.78174593052023
Eb2 18 77.78174593052023
E2  19 82.4068892282175
Es2 20 87.30705785825097
Fb2 19 82.4068892282175
F2  20 87.30705785825097
Fs2 21 92.4986056779086
Gb2 21 92.4986056779086
G2  22 97.99885899543733           //   bass open G
Gs2 23 103.82617439498628
Ab2 23 103.82617439498628
A2  24 110.0
As2 25 116.54094037952248
Bb2 25 116.54094037952248
B2  26 123.47082531403103
Bs2 27 130.8127826502993
Cb3 26 123.47082531403103
C3  27 130.8127826502993
Cs3 28 138.59131548843604
Db3 28 138.59131548843604
D3  29 146.8323839587038
Ds3 30 155.56349186104046
Eb3 30 155.56349186104046
E3  31 164.81377845643496
Es3 32 174.61411571650194
Fb3 31 164.81377845643496
F3  32 174.61411571650194
Fs3 33 184.9972113558172
Gb3 33 184.9972113558172
G3  34 195.99771799087463
Gs3 35 207.65234878997256
Ab3 35 207.65234878997256
A3  36 220.0
As3 37 233.08188075904496
Bb3 37 233.08188075904496
B3  38 246.94165062806206
Bs3 39 261.6255653005986
Cb4 38 246.94165062806206
C4  39 261.6255653005986
Cs4 40 277.1826309768721
Db4 40 277.1826309768721
D4  41 293.6647679174076
Ds4 42 311.1269837220809
Eb4 42 311.1269837220809
E4  43 329.6275569128699
Es4 44 349.2282314330039
Fb4 43 329.6275569128699
F4  44 349.2282314330039
Fs4 45 369.9944227116344
Gb4 45 369.9944227116344
G4  46 391.99543598174927
Gs4 47 415.3046975799451
Ab4 47 415.3046975799451
A4  48 440.0
As4 49 466.1637615180899
Bb4 49 466.1637615180899
B4  50 493.8833012561241
Bs4 51 523.2511306011972
Cb5 50 493.8833012561241
C5  51 523.2511306011972
Cs5 52 554.3652619537442
Db5 52 554.3652619537442
D5  53 587.3295358348151
Ds5 54 622.2539674441618
Eb5 54 622.2539674441618
E5  55 659.2551138257398
Es5 56 698.4564628660078
Fb5 55 659.2551138257398
F5  56 698.4564628660078
Fs5 57 739.9888454232688
Gb5 57 739.9888454232688
G5  58 783.9908719634985
Gs5 59 830.6093951598903
Ab5 59 830.6093951598903
A5  60 880.0
As5 61 932.3275230361799
Bb5 61 932.3275230361799
B5  62 987.7666025122483
Bs5 63 1046.5022612023945
Cb6 62 987.7666025122483
C6  63 1046.5022612023945
Cs6 64 1108.7305239074883
Db6 64 1108.7305239074883
D6  65 1174.6590716696303
Ds6 66 1244.5079348883237
Eb6 66 1244.5079348883237
E6  67 1318.5102276514797
Es6 68 1396.9129257320155
Fb6 67 1318.5102276514797
F6  68 1396.9129257320155
Fs6 69 1479.9776908465376
Gb6 69 1479.9776908465376
G6  70 1567.981743926997
Gs6 71 1661.2187903197805
Ab6 71 1661.2187903197805
A6  72 1760.0
As6 73 1864.6550460723597
Bb6 73 1864.6550460723597
B6  74 1975.533205024496
Bs6 75 2093.004522404789
Cb7 74 1975.533205024496
C7  75 2093.004522404789
Cs7 76 2217.4610478149766
Db7 76 2217.4610478149766
D7  77 2349.31814333926
Ds7 78 2489.0158697766474
Eb7 78 2489.0158697766474
E7  79 2637.02045530296
Es7 80 2793.825851464031
Fb7 79 2637.02045530296
F7  80 2793.825851464031
Fs7 81 2959.955381693075
Gb7 81 2959.955381693075
G7  82 3135.9634878539946
Gs7 83 3322.437580639561
Ab7 83 3322.437580639561
A7  84 3520.0
As7 85 3729.3100921447194
Bb7 85 3729.3100921447194
B7  86 3951.066410048992
Bs7 87 4186.009044809578
Cb8 86 3951.066410048992
C8  87 4186.009044809578


 */
