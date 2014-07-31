package de.mpg.biochem.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.AssertFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:cluster-test-context.xml" })
public class ClusterTest {
	
	@Autowired
	private Job job;
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Before
	public void setup() throws IOException {
		
		String data = "uniprotkb:P37840	uniprotkb:P37840	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:\"MI:0404\"(comigration in non denaturing gel electrophoresis)	Lee et al. (2006)	pubmed:16330551|imex:IM-14509	taxid:9606(human)|taxid:9606(Homo sapiens)	taxid:9606(human)|taxid:9606(Homo sapiens)	psi-mi:\"MI:0407\"(direct interaction)	psi-mi:\"MI:0469\"(IntAct)	intact:EBI-986991|imex:IM-14509-1	intact-miscore:0.98\n"+
				"uniprotkb:P37840	uniprotkb:P37840	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:\"MI:0013\"(biophysical)	Lee et al. (2006)	pubmed:16330551|imex:IM-14509	taxid:9606(human)|taxid:9606(Homo sapiens)	taxid:9606(human)|taxid:9606(Homo sapiens)	psi-mi:\"MI:0407\"(direct interaction)	psi-mi:\"MI:0469\"(IntAct)	intact:EBI-986587|imex:IM-14509-2	intact-miscore:0.98\n"+
				"uniprotkb:P37840	uniprotkb:P37840	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:\"MI:0017\"(classical fluorescence spectroscopy)	Lee et al. (2006)	pubmed:16330551|imex:IM-14509	taxid:9606(human)|taxid:9606(Homo sapiens)	taxid:9606(human)|taxid:9606(Homo sapiens)	psi-mi:\"MI:0407\"(direct interaction)	psi-mi:\"MI:0469\"(IntAct)	intact:EBI-986601|imex:IM-14509-3	intact-miscore:0.98\n"+
				"uniprotkb:P37840	uniprotkb:P37840	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:\"MI:0020\"(transmission electron microscopy)	Lee et al. (2006)	pubmed:16330551|imex:IM-14509	taxid:9606(human)|taxid:9606(Homo sapiens)	taxid:9606(human)|taxid:9606(Homo sapiens)	psi-mi:\"MI:0407\"(direct interaction)	psi-mi:\"MI:0469\"(IntAct)	intact:EBI-985914|imex:IM-14509-4	intact-miscore:0.98\n"+
				"uniprotkb:P37840	uniprotkb:P37840	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:\"MI:0410\"(electron tomography)	Lee et al. (2006)	pubmed:16330551|imex:IM-14509	taxid:9606(human)|taxid:9606(Homo sapiens)	taxid:9606(human)|taxid:9606(Homo sapiens)	psi-mi:\"MI:0407\"(direct interaction)	psi-mi:\"MI:0469\"(IntAct)	intact:EBI-987007|imex:IM-14509-5	intact-miscore:0.98\n"+
				"uniprotkb:P37840	uniprotkb:P37840	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:\"MI:0071\"(molecular sieving)	Lee et al. (2006)	pubmed:16330551|imex:IM-14509	taxid:9606(human)|taxid:9606(Homo sapiens)	taxid:9606(human)|taxid:9606(Homo sapiens)	psi-mi:\"MI:0407\"(direct interaction)	psi-mi:\"MI:0469\"(IntAct)	intact:EBI-987095|imex:IM-14509-6	intact-miscore:0.98\n"+
				"uniprotkb:P37840	uniprotkb:P37840	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	intact:EBI-985879|uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:syua_human(display_long)|uniprotkb:Non-A beta component of AD amyloid(gene name synonym)|uniprotkb:Non-A4 component of amyloid precursor(gene name synonym)|uniprotkb:SNCA(gene name)|psi-mi:SNCA(display_short)|uniprotkb:NACP(gene name synonym)|uniprotkb:PARK1(gene name synonym)	psi-mi:\"MI:0096\"(pull down)	McFarland et al. (2008)	pubmed:18614564|imex:IM-19211	taxid:9606(human)|taxid:9606(Homo sapiens)	taxid:9606(human)|taxid:9606(Homo sapiens)	psi-mi:\"MI:0914\"(association)	psi-mi:\"MI:0469\"(IntAct)	intact:EBI-2935223|imex:IM-19211-8	intact-miscore:0.98\n";		
				
		FileUtils.writeStringToFile(new File("destination.tab"), data);
		
		data = "uniprotkb:P37840	uniprotkb:P37840	intact:EBI-985879	intact:EBI-985879	uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6|uniprotkb:Non-A beta component of AD amyloid|uniprotkb:Non-A4 component of amyloid precursor|uniprotkb:SNCA|uniprotkb:NACP|uniprotkb:PARK1|psi-mi:syua_human|psi-mi:SNCA	uniprotkb:A8K2A4|uniprotkb:Q13701|uniprotkb:Q4JHI3|uniprotkb:Q6IAU6|uniprotkb:Non-A beta component of AD amyloid|uniprotkb:Non-A4 component of amyloid precursor|uniprotkb:SNCA|uniprotkb:NACP|uniprotkb:PARK1|psi-mi:syua_human|psi-mi:SNCA	psi-mi:\"MI:0096\"(pull down)|psi-mi:\"MI:0410\"(electron tomography)|psi-mi:\"MI:0071\"(molecular sieving)|psi-mi:\"MI:0404\"(comigration in non denaturing gel electrophoresis)|psi-mi:\"MI:0013\"(biophysical)|psi-mi:\"MI:0020\"(transmission electron microscopy)|psi-mi:\"MI:0017\"(classical fluorescence spectroscopy)	Lee et al. (2006)|McFarland et al. (2008)	pubmed:16330551|imex:IM-14509|pubmed:18614564|imex:IM-19211	taxid:9606(human)	taxid:9606(human)	psi-mi:\"MI:0914\"(association)|psi-mi:\"MI:0407\"(direct interaction)|psi-mi:\"MI:0407\"(direct interaction)|psi-mi:\"MI:0407\"(direct interaction)|psi-mi:\"MI:0407\"(direct interaction)|psi-mi:\"MI:0407\"(direct interaction)|psi-mi:\"MI:0407\"(direct interaction)	psi-mi:\"MI:0469\"(IntAct)	intact:EBI-985914|intact:EBI-987095|intact:EBI-987007|intact:EBI-986601|intact:EBI-2935223|intact:EBI-986587|intact:EBI-986991	intact-miscore:0.98|score:0.62141585";
		FileUtils.writeStringToFile(new File("clustered.tab"), data);
	}
	
	@Test
    public void testDownloadJob() throws Exception{
		
		String mappingValues = "uniprotkb,intact,ddbj/embl/genbank,chebi,irefindex,hgnc,ensembl";
		
		JobParametersBuilder jobParameters = new JobParametersBuilder();
		jobParameters.addString("fileName", "destination.tab");
		jobParameters.addString("mappings", mappingValues);
		jobParameters.addString("scoreName", "score");
		
		JobExecution execution = jobLauncher.run(job, jobParameters.toJobParameters());
        
		while(!BatchStatus.COMPLETED.equals(execution.getExitStatus())) {
			Thread.sleep(1000);
		}
		
		AssertFile.assertFileEquals(new File("clustered.tab"), new File("destination.tab"));
	}

	@After
	public void teardown() {
		new File("destination.tab").delete();
		new File("destination.tab_clustered").delete();
		new File("destination.tab_sorted").delete();
		new File("clustered.tab").delete();
	}
	
}
