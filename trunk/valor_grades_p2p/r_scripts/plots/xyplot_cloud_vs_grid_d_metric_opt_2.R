# plota o gráfico lattice com o desempenho do grid versus o da cloud baseando-se na métrica D
# vide:
#	generate_ds_d_metric_cis.R
# dependencias:
# imports:
	source( paste(Sys.getenv("VALOR_GRADES_P2P_HOME"),"/r_scripts/setup_name.R",sep=""))
cmd_args = commandArgs(TRUE);
# input:
	# arquivo resultado do script em vide
	inputFile  <- cmd_args[1]
	opt2	<- as.numeric(cmd_args[2])
# output:
	outputFile <- cmd_args[3]

# Increasing grid' size vs m1.small, c1.medium and m2.4xlarge
cisFilePath <- inputFile
FIGURE <- TRUE
pngFilePath <- outputFile

cis <- read.table(cisFilePath, header=T)	

if (FIGURE){
	#png(pngFilePath)#, width=3000, height=4800, res = 600 )
	png(pngFilePath)
}

print(xYplot(Cbind(mean^opt2,lower^opt2,upper^opt2) ~ nPeers,
			groups=type, 
			data=cis, 
			pch="-", type="l", 
			lty=c(3,4,5),
			col = 1,
			panel=function(x,y,...){ 
				panel.xYplot(x, y, ...) ; 
				panel.abline(h = seq(.5,xyplot_cloud_vs_grid_d_metric_max_y,.5), col = c("lightgray","red",rep("lightgray",xyplot_cloud_vs_grid_d_metric_max_y+5)), lty=3, cex=.2)
			}, 
			strip=strip.custom(strip.names=TRUE, strip.levels=TRUE,bg="white",fg="white"),
			ylim=c(xyplot_cloud_vs_grid_d_metric_min_y, xyplot_cloud_vs_grid_d_metric_max_y), 
			main=xyplot_cloud_vs_grid_d_metric_main, 
			xlab=xyplot_cloud_vs_grid_d_metric_xlab, 
			ylab=xyplot_cloud_vs_grid_d_metric_ylab,
			cex=.7
		))

if (FIGURE){
	dev.off()
}
