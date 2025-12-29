package software.ulpgc.kata7.web;

import software.ulpgc.kata7.viewmodel.Histogram;
import io.javalin.http.Context;

public class HistogramController {
    private final HistogramService service;
    private final HistogramAdapter adapter;

    public HistogramController(HistogramService service, HistogramAdapter adapter) {
        this.service = service;
        this.adapter = adapter;
    }

    public void getHistogram(Context ctx) {
        String field = ctx.queryParam("field");
        Integer from = ctx.queryParamAsClass("from", Integer.class).get();
        Integer to = ctx.queryParamAsClass("to", Integer.class).get();

        Histogram histogram = service.buildHistogram(field, from, to);
        HistogramDto dto = adapter.adapt(histogram);
        ctx.json(dto);
    }
}
