delete from vdcgroup_vdcs where vdcgroup_vdcs.vdcgroup_id IN (Select vdcgroup_vdcs.vdcgroup_id from vdcgroup_vdcs, vdcgroup, vdc where vdcgroup_vdcs.vdcgroup_id=vdcgroup.id and vdcgroup_vdcs.vdc_id=vdc.id and vdcgroup.name = 'Dataverses for Individual Scholars' and vdc.dtype = 'Scholar');
delete from vdcgroup where vdcgroup.name = 'Dataverses for Individual Scholars';

