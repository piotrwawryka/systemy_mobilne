package pl.edu.agh.flowers.util;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.List;

import pl.edu.agh.flowers.R;
import pl.edu.agh.flowers.data.Task;
import pl.edu.agh.flowers.data.source.TasksDataSource;
import pl.edu.agh.flowers.data.source.TasksRepository;

// Well..
public class SampleDataProvider {
    private final TasksRepository tasksRepository;

    public SampleDataProvider(TasksRepository tasksRepository) {
        this.tasksRepository = tasksRepository;
    }

    public void fulfillDbWith5SampleFlowers(Resources resources) {
        tasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                if (tasks.size() == 0) fulfillWithSampleFlowers(resources);
            }

            @Override
            public void onDataNotAvailable() {
                fulfillWithSampleFlowers(resources);
            }
        });
    }

    private void fulfillWithSampleFlowers(Resources resources) {
        tasksRepository.saveTask(tulipan(resources));
        tasksRepository.saveTask(roza(resources));
        tasksRepository.saveTask(narcyz(resources));
        tasksRepository.saveTask(gozdzik(resources));
        tasksRepository.saveTask(piwonie(resources));
    }

    private Task piwonie(Resources resources) {
        return new Task(
                "Piwonie",
                "Jedyny rodzaj należący do rodziny piwoniowatych (Paeoniaceae). Należą do niego ok. 33 gatunki. Pochodzą głównie z obszarów Europy i Azji o umiarkowanym klimacie, jedynie 2 gatunki pochodzą z zachodniego wybrzeża Ameryki Północnej.",
                BitmapFactory.decodeResource(resources, R.drawable.piwonie)
        );
    }

    private Task gozdzik(Resources resources) {
        return new Task(
                "Goździk",
                "Rodzaj roślin z rodziny goździkowatych. Obejmuje ok. 300 gatunków z terenu Eurazji i północnej Afryki. W Polsce rośnie dziko 11 gatunków, dość zmiennych morfologicznie.",
                BitmapFactory.decodeResource(resources, R.drawable.gozdzik)
        );
    }

    private Task narcyz(Resources resources) {
        return new Task(
                "Narcyz",
                "Rodzaj roślin należący do rodziny amarylkowatych. Należy do niego kilkadziesiąt gatunków i duża liczba mieszańców. Dziko rosną w krajach śródziemnomorskich, w Europie Środkowej i Północnej oraz w Azji (Chiny, Japonia). Są uprawiane w wielu krajach świata.",
                BitmapFactory.decodeResource(resources, R.drawable.narcyz)
        );
    }

    private Task roza(Resources resources) {
        return new Task(
                "Róża",
                "Róże znane są przede wszystkim jako rośliny ozdobne z powodu efektownych kwiatów. Ze względu na aromat wykorzystywane są jako rośliny kosmetyczne. Walory smakowe i odżywcze owoców szupinkowych róż sprawiają, że róże są także cenionymi roślinami jadalnymi.",
                BitmapFactory.decodeResource(resources, R.drawable.roza)
        );
    }

    private Task tulipan(Resources resources) {
        return new Task(
                "Tulipan",
                "Jest najczęściej uprawianą rośliną ozdobną na świecie. Uprawia się go głównie w gruncie i pod osłonami (szklarnie, tunele foliowe). Nadaje się na rabaty, na obwódki i na kwiat cięty (nie jest zbyt trwały). Najlepsze efekty daje uprawianie go w większych grupach.",
                BitmapFactory.decodeResource(resources, R.drawable.tulipan)
        );

    }

}
