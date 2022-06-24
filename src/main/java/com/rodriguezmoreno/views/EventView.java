package com.rodriguezmoreno.views;

import com.rodriguezmoreno.dto.EventDto;
import com.rodriguezmoreno.entity.Source;
import com.rodriguezmoreno.repository.EventRepository;
import com.rodriguezmoreno.repository.SourceRepository;
import com.rodriguezmoreno.repository.Spec;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.HasClearButton;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

@PageTitle("Eventos")
@Route(value = "events")
@RouteAlias(value = "")
public class EventView extends VerticalLayout {

    private final Grid<EventDto> grid;
    private final DateTimePicker ts1 = new DateTimePicker("Hora de inicio");
    private final DateTimePicker ts2 = new DateTimePicker("Hora de fin");
    private final IntegerField min = new IntegerField("Mínimo");
    private final IntegerField max = new IntegerField("Máximo");
    private final ComboBox<Source> sources = new ComboBox<>("Fuentes");

    public EventView(EventRepository eventRepository, SourceRepository sourceRepository) {
        setSizeFull();

        grid = buildGrid();
        grid.setSizeFull();

        grid.setItems(query -> eventRepository.findDto(Spec.builder()
                        .sourceId(Optional.ofNullable(sources.getValue()).map(Source::getId).orElse(null))
                        .min(Optional.ofNullable(min.getValue()).map(Long::valueOf).orElse(null))
                        .max(Optional.ofNullable(max.getValue()).map(Long::valueOf).orElse(null))
                        .ts1(ts1.getValue())
                        .ts2(ts2.getValue())
                        .build(),
                PageRequest.of(query.getPage(), query.getPageSize(),
                        VaadinSpringDataHelpers.toSpringDataSort(query))).stream());

        sources.setItemLabelGenerator(Source::getSource);

        sources.setItems(query -> {
            final var page = PageRequest.of(query.getPage(), query.getPageSize(),
                    Sort.by("SOURCE").ascending());

            return query.getFilter()
                    .map(s -> sourceRepository.findBySourceContaining(s, page))
                    .orElseGet(() -> sourceRepository.findAll(page)).stream();
        });

        add(new H2("Lista de eventos"), buildToolbar(), grid);
    }

    private Grid<EventDto> buildGrid() {
        final var grid = new Grid<EventDto>();
        final var formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                .withLocale(getLocale());

        grid.addColumn(EventDto::getId, "id")
                .setHeader("ID")
                .setFlexGrow(0);
        grid.addColumn(EventDto::getSource, "source")
                .setHeader("Fuente")
                .setFlexGrow(0);
        grid.addColumn(EventDto::getMeasure, "measure")
                .setHeader("Valor")
                .setTextAlign(ColumnTextAlign.END)
                .setFlexGrow(0);
        grid.addColumn(e -> formatter.format(e.getLocalStamp()), "stamp")
                .setHeader("Tiempo");

        grid.addThemeVariants(GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS);

        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setMultiSort(true);

        grid.getColumns().forEach(c -> {
            c.setResizable(true);
            c.setAutoWidth(true);
        });

        return grid;
    }

    private Component buildToolbar() {
        final var div = new Div();

        div.addClassNames(LumoUtility.Width.FULL, LumoUtility.Display.FLEX, LumoUtility.Gap.SMALL, LumoUtility.FlexWrap.WRAP);
        div.add(configure(sources), configure(ts1), configure(ts2), configure(min), configure(max));

        return div;
    }

    private <T extends AbstractField<?, ?>> T configure(T field) {
        field.addValueChangeListener(e -> updateList());

        if (field instanceof HasClearButton hasClearButton) {
            hasClearButton.setClearButtonVisible(true);
        }

        return field;
    }

    private void updateList() {
        grid.getLazyDataView().refreshAll();
    }
}
