package ohtu.verkkokauppa;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class KauppaTest {

    Pankki pankki;
    Viitegeneraattori viite;
    Varasto varasto;
    Kauppa k;
    
    @Before
    public void setUp() {
        pankki = mock(Pankki.class);
        viite = mock(Viitegeneraattori.class);
        varasto = mock(Varasto.class);
        k = new Kauppa(varasto, pankki, viite);
    }

    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {

        when(viite.uusi()).thenReturn(42);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(), anyInt());
    }
    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaTiedoilla() {

        when(viite.uusi()).thenReturn(42);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), eq("33333-44455"), eq(5));
    }
    
    @Test
    public void ostettaessaKaksiEriTuotettaPankinMetodiaTilisiirtoKutsutaanOikeillaTiedoilla() {

        when(viite.uusi()).thenReturn(42);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "kossu", 15));
        

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "123456");

        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("123456"), eq("33333-44455"), eq(20));
    }
    
    @Test
    public void ostettaessaKaksiSamaaTuotettaPankinMetodiaTilisiirtoKutsutaanOikeillaTiedoilla() {

        when(viite.uusi()).thenReturn(44);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "kossu", 15));
        

        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "123456");

        verify(pankki).tilisiirto(eq("pekka"), eq(44), eq("123456"), eq("33333-44455"), eq(30));
    }
    
    @Test
    public void varastostaLoppunuttaTuotettaOStetaessaPankinMetodiaTilisiirtoKutsutaanOikeillaTiedoilla() {

        when(viite.uusi()).thenReturn(45);

        when(varasto.saldo(1)).thenReturn(0);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "kossu", 15));
        

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "123456");

        verify(pankki).tilisiirto(eq("pekka"), eq(45), eq("123456"), eq("33333-44455"), eq(15));
    }
    
    @Test
    public void aloitaAsiointiNollaaEdellisenOstoksenTiedot() {

        when(viite.uusi()).thenReturn(45);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "kossu", 15));
        

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.tilimaksu("pekka", "123456");

        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(), eq(15));
    }
    
    @Test
    public void jokaiselleMaksutapahtumallePyydetaanOmaViite() {

        when(viite.uusi()).thenReturn(45, 46);

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "kossu", 15));
        

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "123456");
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.tilimaksu("pirjo", "123456");

        verify(viite, times(2)).uusi();
    }
    
    @Test
    public void koristaPoistaminenPalauttaaTuotteenVarastoon() {

        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.poistaKorista(1);

        verify(varasto).palautaVarastoon(eq(new Tuote(1, "maito", 5)));
    }
}
