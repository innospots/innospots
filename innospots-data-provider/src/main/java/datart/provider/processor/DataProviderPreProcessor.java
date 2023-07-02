package datart.provider.processor;

import datart.base.processor.ExtendProcessor;
import datart.base.processor.ProcessorResponse;
import datart.provider.DataProviderSource;
import datart.provider.ExecuteParam;
import datart.provider.QueryScript;

public interface DataProviderPreProcessor extends ExtendProcessor {
    ProcessorResponse preRun(DataProviderSource config, QueryScript script, ExecuteParam executeParam);
}
