package pl.edu.agh.flowers.util;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import java.util.List;

import pl.edu.agh.flowers.R;
import pl.edu.agh.flowers.data.Flower;
import pl.edu.agh.flowers.data.source.FlowersDataSource;
import pl.edu.agh.flowers.data.source.FlowersRepository;

// Well..
public class SampleDataProvider {
    private final FlowersRepository flowersRepository;

    public SampleDataProvider(FlowersRepository flowersRepository) {
        this.flowersRepository = flowersRepository;
    }

    public void fulfillDbWith5SampleFlowers(Resources resources) {
        flowersRepository.getFlowers(new FlowersDataSource.LoadTasksCallback() {
            @Override
            public void onFlowersLoaded(List<Flower> tasks) {
                if (tasks.size() == 0) fulfillWithSampleFlowers(resources);
            }

            @Override
            public void onDataNotAvailable() {
                fulfillWithSampleFlowers(resources);
            }
        });
    }

    private void fulfillWithSampleFlowers(Resources resources) {
        flowersRepository.saveFlower(tulipan(resources));
        flowersRepository.saveFlower(roza(resources));
        flowersRepository.saveFlower(narcyz(resources));
        flowersRepository.saveFlower(gozdzik(resources));
        flowersRepository.saveFlower(piwonie(resources));
    }

    private Flower piwonie(Resources resources) {
        return new Flower(
                "Piwonie",
                "Jedyny rodzaj należący do rodziny piwoniowatych (Paeoniaceae). Należą do niego ok. 33 gatunki. Pochodzą głównie z obszarów Europy i Azji o umiarkowanym klimacie, jedynie 2 gatunki pochodzą z zachodniego wybrzeża Ameryki Północnej.",
                BitmapFactory.decodeResource(resources, R.drawable.piwonie)
        );
    }

    private Flower gozdzik(Resources resources) {
        return new Flower(
                "Goździk",
                "Rodzaj roślin z rodziny goździkowatych. Obejmuje ok. 300 gatunków z terenu Eurazji i północnej Afryki. W Polsce rośnie dziko 11 gatunków, dość zmiennych morfologicznie.",
                BitmapFactory.decodeResource(resources, R.drawable.gozdzik)
        );
    }

    private Flower narcyz(Resources resources) {
        return new Flower(
                "Narcyz",
                "Rodzaj roślin należący do rodziny amarylkowatych. Należy do niego kilkadziesiąt gatunków i duża liczba mieszańców. Dziko rosną w krajach śródziemnomorskich, w Europie Środkowej i Północnej oraz w Azji (Chiny, Japonia). Są uprawiane w wielu krajach świata.",
                BitmapFactory.decodeResource(resources, R.drawable.narcyz)
        );
    }

    private Flower roza(Resources resources) {
        return new Flower(
                "Róża",
                "Róże znane są przede wszystkim jako rośliny ozdobne z powodu efektownych kwiatów. Ze względu na aromat wykorzystywane są jako rośliny kosmetyczne. Walory smakowe i odżywcze owoców szupinkowych róż sprawiają, że róże są także cenionymi roślinami jadalnymi.",
                BitmapFactory.decodeResource(resources, R.drawable.roza)
        );
    }

    private Flower tulipan(Resources resources) {
        return new Flower(
                "Tulipan",
                "Jest najczęściej uprawianą rośliną ozdobną na świecie. Uprawia się go głównie w gruncie i pod osłonami (szklarnie, tunele foliowe). Nadaje się na rabaty, na obwódki i na kwiat cięty (nie jest zbyt trwały). Najlepsze efekty daje uprawianie go w większych grupach.",
                BitmapFactory.decodeResource(resources, R.drawable.tulipan)
        );

    }

}
