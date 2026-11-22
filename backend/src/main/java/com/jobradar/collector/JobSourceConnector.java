package com.jobradar.collector;

import com.jobradar.domain.JobListing;
import java.util.List;

/** Connect only to documented job APIs/feeds whose terms permit this use. */
public interface JobSourceConnector {
    String sourceName();
    List<JobListing> fetchJobs();

    /** Sources may override this to perform an on-demand search for a requested city. */
    default List<JobListing> fetchJobs(String city) {
        return fetchJobs();
    }
}