package de.mpg.biochem;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.mpg.biochem.model.ServiceHandler;

public class Main {
	
	private static ApplicationContext ctx = null;
	
	public static void main(String[] args) throws IOException, ParseException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		
		
		//Download params
		Option download = OptionBuilder.withArgName("download")
				.withLongOpt("download")
				.withDescription("Download interactions from PSICQUIC Servers")
				.create("dwl");
		
		Option services = OptionBuilder.withArgName("services")
				.withDescription("with -dwl list of services to query")
				.hasOptionalArgs()
				.create("services");
		
		
		Option query = OptionBuilder.withArgName("query")
				.withLongOpt("query")
				.withDescription("with -dwl PSICQUIC query to use")
				.hasArg()
				.create("q");
		
		//Cluster
		Option cluster = OptionBuilder.withArgName("cluster")
				.withLongOpt("cluster")
				.withDescription("cluster file")
				.create("cl");
		
		
		Option mappings = OptionBuilder.withArgName("mappings")
				.withDescription("with -cl priority for molecule accession mapping")
				.hasArg()
				.create("mappings");
		
		Option nameScore = OptionBuilder.withArgName("sn")
				.withDescription("with -cl score name")
				.hasArg()
				.create("sn");
		
		//Map
		Option map = OptionBuilder.withArgName("map")
				.withDescription("map identifiers to uniprot")
				.create("map");
		
		Option i = OptionBuilder.withArgName("i")
				.withLongOpt("interactions")
				.withDescription("if present will map interactions")
				.create("i");
		
		Option mapPath = OptionBuilder.withArgName("p")
				.withLongOpt("path")
				.withDescription("mapping db folder. If it does not exists, it will be created")
				.hasArg()
				.create("p");
		
		//merge
		Option merge = OptionBuilder.withArgName("m")
					.withLongOpt("merge")
					.withDescription("merge MITab files")
					.create("m");
		
		Option file = OptionBuilder.withArgName("file")
				.withDescription("input file")
				.hasArg()
				.create("file");
		
		Option output = OptionBuilder.withArgName("o")
				.withLongOpt("out")
				.withDescription("output folder")
				.hasArg()
				.create("o");
		
		//help
		Option help = new Option("help", "print this message");
		
		Options options = new Options();
		options.addOption(help);
		options.addOption(download);
		options.addOption(services);
		options.addOption(query);
		options.addOption(output);
		options.addOption(cluster);
		options.addOption(file);
		options.addOption(nameScore);
		options.addOption(mappings);
		options.addOption(map);
		options.addOption(i);
		options.addOption(merge);
		
		
		CommandLineParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);
	    
		try {
			if (line.hasOption("help")){
				HelpFormatter formatter = new HelpFormatter();
	            formatter.printHelp("PSICQUICHelper", options);
			}else if (line.hasOption("dwl")){
				//Download interactions
				
				String path = "", queryStr = "*", serviceLst = "";
				if(line.hasOption("o")) {
					path = line.getOptionValue("o");
					
					path = (path.endsWith("/")) ? path : path + "/";
					FileUtils.forceMkdir(new File(path));
				}
				
				if(line.hasOption("services")){
					serviceLst = StringUtils.join(line.getOptionValues("services"), ",");
				}
				
				if(line.hasOption("q")){
					queryStr = line.getOptionValue("q");
				}
				
				ctx = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/download-context.xml");
				
				ServiceHandler serviceHandler = ctx.getBean(ServiceHandler.class);
				serviceHandler.setPath(path+"services.xml");
				
				Job job = (Job) ctx.getBean("downloadJob");
				JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
				
				JobParametersBuilder jobParameters = new JobParametersBuilder();
				jobParameters.addString("path", path);
				jobParameters.addString("services", serviceLst);
				jobParameters.addString("query", queryStr);
				
				jobLauncher.run(job, jobParameters.toJobParameters());
				
			}else if(line.hasOption("cl")){
				//Cluster file
				
				if(!line.hasOption("file")) { 
					throw new IllegalArgumentException("-file is not defined");
				}
				
				ctx = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/cluster-context.xml");
				
				String mappingValues = "uniprotkb,intact,ddbj/embl/genbank,chebi,irefindex,hgnc,ensembl",
						scoreName = "miscore";
				
				if(line.hasOption("mappings")){
					mappingValues = line.getOptionValue("mappings");
				}
				
				if(line.hasOption("sn")){
					scoreName = line.getOptionValue("sn");
				}
				
				Job job = (Job) ctx.getBean("clusterJob");
				JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
				
				JobParametersBuilder jobParameters = new JobParametersBuilder();
				jobParameters.addString("fileName", line.getOptionValue("file"));
				jobParameters.addString("mappings", mappingValues);
				jobParameters.addString("scoreName", scoreName);
				
				jobLauncher.run(job, jobParameters.toJobParameters());
				
			}else if(line.hasOption("map")) {
				
				if(!line.hasOption("file")) { 
					throw new IllegalArgumentException("-file is not defined");
				}
				
				if(line.hasOption("i")) {
					ctx = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/mapping-context.xml");
				}else {
					ctx = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/id-mapping-context.xml");
				}
				
				String out = "", 
						uniprotUrl = "ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/idmapping_selected.tab.gz", 
						tarUrl = "ftp://ftp.arabidopsis.org/home/tair/Proteins/Id_conversions/Uniprot_TAIR10_May2012.txt";
				
				if(line.hasOption("o")) {
					out = line.getOptionValue("o");
					
					out = (out.endsWith("/")) ? out : out + "/";
					FileUtils.forceMkdir(new File(out));
				}
				
				Job job = (Job) ctx.getBean("mapJob");
				JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
				
				JobParametersBuilder jobParameters = new JobParametersBuilder();
				jobParameters.addString("fileName", line.getOptionValue("file"));
				jobParameters.addString("mappingPath", out);
				jobParameters.addString("uniprotUrl", uniprotUrl);
				jobParameters.addString("tarUrl", tarUrl);
				
				jobLauncher.run(job, jobParameters.toJobParameters());
						
			}else if(line.hasOption("m")) {
				
				if(!line.hasOption("file")) { 
					throw new IllegalArgumentException("-file is not defined");
				}
				
				ctx = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/merge-context.xml");
				
				Job job = (Job) ctx.getBean("mergeJob");
				JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
				
				JobParametersBuilder jobParameters = new JobParametersBuilder();
				jobParameters.addString("fileName", line.getOptionValue("file"));
				
				jobLauncher.run(job, jobParameters.toJobParameters());
				
			}else {
				HelpFormatter formatter = new HelpFormatter();
	            formatter.printHelp("PSICQUICHelper", options);
			}
		}catch(IllegalArgumentException e) {
			HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("PSICQUICHelper", options);
		}
	}
}