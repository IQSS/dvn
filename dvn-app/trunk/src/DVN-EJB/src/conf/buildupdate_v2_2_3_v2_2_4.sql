ALTER TABLE datatable ADD COLUMN visualizationShowDataTable character varying(255);
ALTER TABLE datatable ADD COLUMN visualizationShowImageGraph character varying(255);
ALTER TABLE datatable ADD COLUMN visualizationShowFlashGraph character varying(255);
ALTER TABLE datatable ADD COLUMN visualizationSourceInfoLabel character varying(255);

UPDATE datatable set visualizationShowDataTable = 'available', 
visualizationShowImageGraph = 'available', 
visualizationShowFlashGraph = 'available'; 