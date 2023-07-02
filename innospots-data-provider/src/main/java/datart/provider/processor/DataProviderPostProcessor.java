package datart.provider.processor;

import datart.base.processor.ExtendProcessor;
import datart.base.processor.ProcessorResponse;
import datart.provider.DataProviderSource;
import datart.provider.Dataframe;
import datart.provider.ExecuteParam;
import datart.provider.QueryScript;

public interface DataProviderPostProcessor extends ExtendProcessor {
    ProcessorResponse postRun(Dataframe frame, DataProviderSource config, QueryScript script, ExecuteParam executeParam);
}
